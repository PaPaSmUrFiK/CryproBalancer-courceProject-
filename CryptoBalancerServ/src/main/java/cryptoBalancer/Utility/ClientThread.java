package cryptoBalancer.Utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cryptoBalancer.Adapters.LocalDateAdapter;
import cryptoBalancer.Adapters.LocalDateTimeAdapter;
import cryptoBalancer.Enums.ResponseStatus;
import cryptoBalancer.Models.Entities.User;
import cryptoBalancer.Services.*;
import cryptoBalancer.Models.TCP.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                User user = gson.fromJson(request.getRequestMessage(), User.class);
                System.out.println("Регистрация пользователя: " + user.getUsername());

                List<User> users = userService.findAllEntities();
                boolean usernameExists = users.stream()
                        .anyMatch(existingUser -> existingUser.getUsername().equals(user.getUsername()));

                if (!usernameExists) {
                    userService.saveEntity(user);
                    User savedUser = userService.findEntity(user.getUserId());
                    return new Response(ResponseStatus.OK, "Пользователь успешно зарегистрирован!", gson.toJson(savedUser));
                } else {
                    return new Response(ResponseStatus.ERROR, "Такой пользователь уже существует.", "");
                }
            }
            case LOGIN: {
                User requestUser = gson.fromJson(request.getRequestMessage(), User.class);
                System.out.println("Попытка входа пользователя: " + requestUser.getUsername());

                List<User> users = userService.findAllEntities();
                boolean validUser = users.stream()
                        .anyMatch(u ->
                                u.getUsername().equals(requestUser.getUsername()) &&
                                        PasswordUtils.checkPassword(requestUser.getPasswordHash(), u.getPasswordHash())
                        );

                if (validUser) {
                    User user = users.stream()
                            .filter(u -> u.getUsername().equals(requestUser.getUsername()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Пользователь не найден после проверки"));

                    user = userService.findEntity(user.getUserId());
                    System.out.println("_______________________________");
                    System.out.println(user);// обновляем полные данные
                    return new Response(ResponseStatus.OK, "Успешный вход!", gson.toJson(user));
                } else {
                    return new Response(ResponseStatus.ERROR, "Неверное имя пользователя или пароль.", "");
                }
            }
            default:
                return new Response(ResponseStatus.ERROR, "Неизвестный тип запроса.", "");
        }
    }
}
