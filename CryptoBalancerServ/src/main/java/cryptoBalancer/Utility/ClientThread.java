package cryptoBalancer.Utility;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import cryptoBalancer.Adapters.LocalDateAdapter;
import cryptoBalancer.Adapters.LocalDateTimeAdapter;
import cryptoBalancer.Enums.ResponseStatus;
import cryptoBalancer.Models.Entities.*;
import cryptoBalancer.Services.*;
import cryptoBalancer.Models.TCP.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;

    private AnaliticService analiticService = new AnaliticService();
    private CryptoService cryptoService = new CryptoService();
    private CryptoHistoryService cryptoHistoryService = new CryptoHistoryService();
    private InversmentService inversmentService = new InversmentService();
    private PortfolioService portfolioService = new PortfolioService();
    private RoleService roleService = new RoleService();
    private UserService userService = new UserService();


    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }


    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                try {
                    Request request = gson.fromJson(message, Request.class);
                    Response response = handleRequest(request);
                    out.println(gson.toJson(response));
                    out.flush();
                } catch (JsonSyntaxException e) {
                    System.out.println("Ошибка парсинга JSON: " + e.getMessage());
                    Response errorResponse = new Response(ResponseStatus.ERROR, "Ошибка формата сообщения!", "");
                    out.println(gson.toJson(errorResponse));
                    out.flush();
                } catch (Exception e) {
                    System.out.println("Ошибка обработки запроса: " + e.getMessage());
                    Response errorResponse = new Response(ResponseStatus.ERROR, "Внутренняя ошибка сервера.", "");
                    out.println(gson.toJson(errorResponse));
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Соединение с клиентом разорвано: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }

    }

    private Response handleRequest(Request request) {
        switch (request.getRequestType()) {
            case REGISTER: {
                try {
                    User user = gson.fromJson(request.getRequestMessage(), User.class);
                    System.out.println("Регистрация пользователя: " + (user != null ? user.getUsername() : "null"));

                    if (user == null || user.getUsername() == null || user.getPasswordHash() == null) {
                        return new Response(ResponseStatus.ERROR, "Некорректные данные пользователя", "");
                    }

                    user.setPasswordHash(PasswordUtils.hashPassword(user.getPasswordHash()));
                    userService.saveEntity(user);
                    User savedUser = userService.findEntity(user.getUserId());
                    return savedUser != null
                            ? new Response(ResponseStatus.OK, "Пользователь успешно зарегистрирован", gson.toJson(savedUser))
                            : new Response(ResponseStatus.ERROR, "Ошибка при сохранении пользователя", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Внутренняя ошибка сервера: " + e.getMessage(), "");
                }
            }
            case LOGIN: {
                try {
                    User requestUser = gson.fromJson(request.getRequestMessage(), User.class);
                    System.out.println("Попытка входа пользователя: " + (requestUser != null ? requestUser.getUsername() : "null"));

                    if (requestUser == null || requestUser.getUsername() == null || requestUser.getPasswordHash() == null) {
                        return new Response(ResponseStatus.ERROR, "Логин и пароль обязательны", "");
                    }

                    List<User> users = userService.findAllEntities();
                    if (users == null) {
                        return new Response(ResponseStatus.ERROR, "Ошибка при получении списка пользователей", "");
                    }

                    User foundUser = users.stream()
                            .filter(u -> u.getUsername().equals(requestUser.getUsername()) &&
                                    PasswordUtils.checkPassword(requestUser.getPasswordHash(), u.getPasswordHash()))
                            .findFirst()
                            .orElse(null);

                    return foundUser != null
                            ? new Response(ResponseStatus.OK, "Успешный вход", gson.toJson(foundUser))
                            : new Response(ResponseStatus.ERROR, "Неверное имя пользователя или пароль", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Внутренняя ошибка сервера: " + e.getMessage(), "");
                }
            }
            case GET_CRYPTO_LIST: {
                try {
                    List<Crypto> cryptoList = cryptoService.findAllEntities();
                    String[] cryptoNameList = cryptoList.stream()
                            .map(Crypto::getName)
                            .toArray(String[]::new);
                    return new Response(ResponseStatus.OK, "Список криптовалют успешно загружен", gson.toJson(cryptoNameList));
                } catch (Exception e) {
                    return new Response(ResponseStatus.ERROR, "Ошибка при загрузке списка криптовалют: " + e.getMessage(), "");
                }
            }
            case GET_CRYPTO_DATA:{
                try {
                    String cryptoName = gson.fromJson(request.getRequestMessage(), String.class);
                    if (cryptoName == null || cryptoName.trim().isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Имя криптовалюты не указано", "");
                    }

                    Crypto foundCrypto = cryptoService.findCryptoByName(cryptoName);
                    if (foundCrypto == null) {
                        return new Response(ResponseStatus.ERROR, "Криптовалюта не найдена: " + cryptoName, "");
                    }

                    List<CryptoHistory> history = foundCrypto.getHistory();
                    if (history == null || history.isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "История для криптовалюты не найдена: " + cryptoName, "");
                    }

                    return new Response(ResponseStatus.OK, "История криптовалюты успешно загружена", gson.toJson(history));
                } catch (Exception e) {
                    return new Response(ResponseStatus.ERROR, "Ошибка при обработке запроса: " + e.getMessage(), "");
                }
            }
            case CHANGE_PASSWORD:{
                try {
                    User changeUser = gson.fromJson(request.getRequestMessage(), User.class);
                    int userId = changeUser.getUserId();
                    String newPassword = changeUser.getPasswordHash();

                    User user = userService.findEntity(userId);
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                    }

                    user.setPasswordHash(PasswordUtils.hashPassword(newPassword));
                    userService.updateEntity(user);
                    return new Response(ResponseStatus.OK, "Пароль успешно изменён", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при смене пароля: " + e.getMessage(), "");
                }
            }
            case GET_PORTFOLIO_LIST:{
                try {
                    Integer userId = gson.fromJson(request.getRequestMessage(), Integer.class);
                    if (userId == null) {
                        return new Response(ResponseStatus.ERROR, "userId не указан", "");
                    }

                    User user = userService.findEntity(userId);
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                    }

                    List<Portfolio> portfolios = user.getPortfolios();
                    String[] portfolioNameList = portfolios.stream()
                            .map(Portfolio::getPortfolioName)
                            .toArray(String[]::new);
                    return new Response(ResponseStatus.OK, "Список портфелей успешно загружен", gson.toJson(portfolioNameList));
                } catch (JsonSyntaxException e) {
                    return new Response(ResponseStatus.ERROR, "Ошибка при парсинге userId: " + e.getMessage(), "");
                } catch (Exception e) {
                    return new Response(ResponseStatus.ERROR, "Ошибка при загрузке списка портфелей: " + e.getMessage(), "");
                }
            }
            case SAVE_PORTFOLIO:{
                try {
                    Portfolio portfolio = gson.fromJson(request.getRequestMessage(), Portfolio.class);
                    if (portfolio == null || portfolio.getPortfolioName() == null || portfolio.getPortfolioName().trim().isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Имя портфеля не может быть пустым", null);
                    }

                    int userId = portfolio.getUser().getUserId();
                    User user = userService.findEntity(userId);
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", null);
                    }

                    Portfolio existingPortfolio = user.getPortfolios().stream()
                            .filter(p -> p.getPortfolioName().equals(portfolio.getPortfolioName()))
                            .findFirst()
                            .orElse(null);

                    Portfolio portfolioToSave;
                    if (existingPortfolio != null) {
                        portfolioToSave = existingPortfolio;
                        portfolioToSave.setCreatedAt(LocalDateTime.now());
                    } else {
                        portfolioToSave = new Portfolio();
                        portfolioToSave.setPortfolioName(portfolio.getPortfolioName());
                        portfolioToSave.setUser(user);
                        portfolioToSave.setCreatedAt(LocalDateTime.now());

                        // Создаем аналитику
                        Analytic analytic = new Analytic();
                        analytic.setExpectedReturn(BigDecimal.ZERO);
                        analytic.setRisk(BigDecimal.ZERO);
                        
                        // Устанавливаем двустороннюю связь
                        analytic.setPortfolio(portfolioToSave);
                        portfolioToSave.setAnalytic(analytic);
                        
                        // Сохраняем портфель с аналитикой одной транзакцией
                        try {
                            portfolioService.saveEntity(portfolioToSave);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new IllegalStateException("Ошибка при сохранении портфеля: " + e.getMessage());
                        }
                    }

                    if (existingPortfolio != null) {
                        inversmentService.deleteInvestmentsByPortfolioId(existingPortfolio.getPortfolioId());
                    }

                    if (portfolio.getInvestments() != null) {
                        for (Investment clientInvestment : portfolio.getInvestments()) {
                            String cryptoName = clientInvestment.getCrypto().getName();
                            Crypto crypto = cryptoService.findCryptoByName(cryptoName);
                            if (crypto == null) {
                                return new Response(ResponseStatus.ERROR, "Криптовалюта '" + cryptoName + "' не найдена", null);
                            }

                            Investment investment = new Investment();
                            investment.setPortfolio(portfolioToSave);
                            investment.setCrypto(crypto);
                            investment.setAmount(clientInvestment.getAmount());
                            investment.setPurchasePrice(clientInvestment.getPurchasePrice());
                            inversmentService.saveEntity(investment);
                        }
                    }

                    Portfolio fullPortfolio = portfolioService.findEntity(portfolioToSave.getPortfolioId());
                    return fullPortfolio != null
                            ? new Response(ResponseStatus.OK, "Портфель успешно сохранён", gson.toJson(fullPortfolio))
                            : new Response(ResponseStatus.ERROR, "Не удалось загрузить сохранённый портфель", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Не удалось сохранить портфель: " + e.getMessage(), "");
                }
            }

            case GET_ALL_CRYPTO: {
                try {
                    List<Crypto> allCryptos = cryptoService.findAllEntities();
                    Type cryptoListType = new TypeToken<List<Crypto>>() {}.getType();
                    String jsonData = gson.toJson(allCryptos, cryptoListType);
                    return allCryptos != null && !allCryptos.isEmpty()
                            ? new Response(ResponseStatus.OK, "Список криптовалют успешно загружен", jsonData)
                            : new Response(ResponseStatus.ERROR, "Список криптовалют пуст", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка загрузки списка криптовалют: " + e.getMessage(), "");
                }
            }

            case GET_ALL_USERS: {
                try {
                    List<User> allUsers = userService.findAllEntities();
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    String jsonData = gson.toJson(allUsers, userListType);
                    return allUsers != null && !allUsers.isEmpty()
                            ? new Response(ResponseStatus.OK, "Список пользователей успешно загружен", jsonData)
                            : new Response(ResponseStatus.ERROR, "Список пользователей пуст", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка загрузки списка пользователей: " + e.getMessage(), "");
                }
            }

            case GET_CRYPTO_NEWS: {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest newsRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://min-api.cryptocompare.com/data/v2/news/?lang=EN"))
                            .build();

                    HttpResponse<String> response = client.send(newsRequest, HttpResponse.BodyHandlers.ofString());
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray data = jsonResponse.getAsJsonArray("Data");

                    List<News> newsItems = new ArrayList<>();
                    for (int i = 0; i < Math.min(10, data.size()); i++) {
                        JsonObject newsObj = data.get(i).getAsJsonObject();
                        News news = new News();
                        news.setTitle(newsObj.get("title").getAsString());
                        news.setUrl(newsObj.get("url").getAsString());
                        long timestamp = newsObj.get("published_on").getAsLong();
                        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
                        news.setPublishedOn(dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                        news.setDescription(newsObj.get("body").getAsString().substring(0, Math.min(100, newsObj.get("body").getAsString().length())) + "...");
                        newsItems.add(news);
                    }

                    Type newsListType = new TypeToken<List<News>>(){}.getType();
                    String jsonData = gson.toJson(newsItems, newsListType);
                    return new Response(ResponseStatus.OK, "Новости успешно загружены", jsonData);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка загрузки новостей: " + e.getMessage(), "");
                }
            }
            case DELETE_USER: {
                try {
                    Integer userId = gson.fromJson(request.getRequestMessage(), Integer.class);
                    if (userId == null) {
                        return new Response(ResponseStatus.ERROR, "Идентификатор пользователя не указан", "");
                    }

                    User user = userService.findEntity(userId);
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь с ID " + userId + " не найден", "");
                    }

                    userService.deleteEntity(user);
                    // Проверяем, что пользователь действительно удалён
                    User deletedUser = userService.findEntity(userId);
                    if (deletedUser == null) {
                        return new Response(ResponseStatus.OK, "Пользователь успешно удалён", "");
                    } else {
                        return new Response(ResponseStatus.ERROR, "Не удалось удалить пользователя", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при удалении пользователя: " + e.getMessage(), "");
                }
            }
            case UPDATE_USER: {
                try {
                    User updatedUser = gson.fromJson(request.getRequestMessage(), User.class);
                    if (updatedUser == null || updatedUser.getUserId() == 0) {
                        return new Response(ResponseStatus.ERROR, "Некорректные данные пользователя", "");
                    }

                    User existingUser = userService.findEntity(updatedUser.getUserId());
                    if (existingUser == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь с ID " + updatedUser.getUserId() + " не найден", "");
                    }

                    // Обновляем поля пользователя
                    if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
                        existingUser.setUsername(updatedUser.getUsername());
                    }
                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getRole() != null && updatedUser.getRole().getRoleName() != null) {
                        // Находим роль по имени
                        Role role = roleService.findByName(updatedUser.getRole().getRoleName());
                        if (role == null) {
                            return new Response(ResponseStatus.ERROR, "Роль '" + updatedUser.getRole().getRoleName() + "' не найдена", "");
                        }
                        existingUser.setRole(role);
                    }

                    userService.updateEntity(existingUser);
                    return new Response(ResponseStatus.OK, "Данные пользователя успешно обновлены", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при обновлении данных пользователя: " + e.getMessage(), "");
                }
            }
            case UPDATE_USER_ROLE: {
                try {
                    User updatedUser = gson.fromJson(request.getRequestMessage(), User.class);
                    if (updatedUser == null || updatedUser.getUserId() == 0 || updatedUser.getRole() == null) {
                        return new Response(ResponseStatus.ERROR, "Некорректные данные пользователя или роли", "");
                    }

                    User existingUser = userService.findEntity(updatedUser.getUserId());
                    if (existingUser == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь с ID " + updatedUser.getUserId() + " не найден", "");
                    }

                    String roleName = updatedUser.getRole().getRoleName();
                    Role role = roleService.findByName(roleName);
                    if (role == null) {
                        return new Response(ResponseStatus.ERROR, "Роль '" + roleName + "' не найдена", "");
                    }

                    existingUser.setRole(role);
                    userService.updateEntity(existingUser);
                    return new Response(ResponseStatus.OK, "Роль пользователя успешно обновлена", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при обновлении роли пользователя: " + e.getMessage(), "");
                }
            }
            case GET_PORTFOLIO_DATA: {
                try {
                    Portfolio requestData = gson.fromJson(request.getRequestMessage(), Portfolio.class);
                    String portfolioName = requestData.getPortfolioName();
                    User user = requestData.getUser();


                    if (portfolioName == null || portfolioName.trim().isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Название портфеля не указано", "");
                    }

                    user = userService.findEntity(user.getUserId());

                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                    }

                    Portfolio portfolio = user.getPortfolios().stream()
                            .filter(p -> p.getPortfolioName().equals(portfolioName))
                            .findFirst()
                            .orElse(null);

                    if (portfolio == null) {
                        return new Response(ResponseStatus.ERROR, "Портфель не найден: " + portfolioName, "");
                    }

                    String portfolioJson = gson.toJson(portfolio);
                    System.out.println("-------------");
                    System.out.println(portfolioJson);
                    return new Response(ResponseStatus.OK, "Данные портфеля успешно загружены", portfolioJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при загрузке данных портфеля: " + e.getMessage(), "");
                }
            }
            case DELETE_PORTFOLIO: {
                try {
                    Portfolio deletedPOrtfolio = gson.fromJson(request.getRequestMessage(), Portfolio.class);
                    String portfolioName = deletedPOrtfolio.getPortfolioName();

                    if (portfolioName == null || portfolioName.trim().isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Название портфеля не указано", "");
                    }

                    User user = deletedPOrtfolio.getUser();
                    user = userService.findEntity(user.getUserId());
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                    }

                    Portfolio portfolio = user.getPortfolios().stream()
                            .filter(p -> p.getPortfolioName().equals(portfolioName))
                            .findFirst()
                            .orElse(null);

                    if (portfolio == null) {
                        return new Response(ResponseStatus.ERROR, "Портфель не найден: " + portfolioName, "");
                    }

                    portfolioService.deleteEntity(portfolio);

                    return new Response(ResponseStatus.OK, "Портфель успешно удалён", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Ошибка при удалении портфеля: " + e.getMessage(), "");
                }
            }
            case ANALYZE_PORTFOLIO: {
                try {
                    // Десериализация портфеля из запроса
                    Portfolio deletedPortfolio = gson.fromJson(request.getRequestMessage(), Portfolio.class);
                    String portfolioName = deletedPortfolio.getPortfolioName();

                    // Проверка имени портфеля
                    if (portfolioName == null || portfolioName.trim().isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Название портфеля не указано", "");
                    }

                    // Поиск пользователя
                    User user = deletedPortfolio.getUser();
                    user = userService.findEntity(user.getUserId());
                    if (user == null) {
                        return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                    }

                    // Поиск портфеля по имени
                    Portfolio portfolio = user.getPortfolios().stream()
                            .filter(p -> p.getPortfolioName().equals(portfolioName))
                            .findFirst()
                            .orElse(null);

                    if (portfolio == null) {
                        return new Response(ResponseStatus.ERROR, "Портфель не найден: " + portfolioName, "");
                    }

                    // Получение инвестиций и общего капитала
                    Set<Investment> investments = portfolio.getInvestments();
                    if (investments.isEmpty()) {
                        return new Response(ResponseStatus.ERROR, "Портфель не содержит инвестиций", "");
                    }

                    // Оптимизация портфеля
                    MarkowitzOptimizer markowitzOptimizer = new MarkowitzOptimizer(cryptoHistoryService);
                    if (portfolio.getAnalytic().getExpectedReturn().compareTo(BigDecimal.ZERO) != 0) {
                        double minReturn = portfolio.getAnalytic().getExpectedReturn().doubleValue(); // Предполагаем, что risk здесь означает целевую доходность
                        markowitzOptimizer.minimizeRiskForReturn(investments, minReturn);
                    } else {
                        // Максимизация доходности при заданном уровне риска
                        double maxVariance = portfolio.getAnalytic().getRisk().doubleValue();
                        markowitzOptimizer.maximizeReturnForRisk(investments, maxVariance);
                    }

                    // Сохранение обновленного портфеля в базе данных
                    portfolioService.updateEntity(portfolio); // Предполагаем, что есть portfolioService
                    System.out.println(portfolio.getInvestments());
                    // Формирование ответа
                    return new Response(ResponseStatus.OK, "Портфель оптимизирован", gson.toJson(portfolio));
                } catch (IllegalStateException e) {
                    // Ошибка оптимизации (например, недостаточно данных или решение не найдено)
                    return new Response(ResponseStatus.ERROR, "Ошибка оптимизации: " + e.getMessage(), "");
                } catch (Exception e) {
                    return new Response(ResponseStatus.ERROR, "Ошибка сервера: " + e.getMessage(), "");
                }
            }
            default:
                return new Response(ResponseStatus.ERROR, "Неизвестный тип запроса.", "");
        }
    }
}
