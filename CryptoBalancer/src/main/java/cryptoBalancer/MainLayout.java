package cryptoBalancer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cryptoBalancer.Adapters.LocalDateAdapter;
import cryptoBalancer.Adapters.LocalDateTimeAdapter;
import cryptoBalancer.Adapters.NewsListCell;
import cryptoBalancer.Enums.RequestType;
import cryptoBalancer.Enums.ResponseStatus;
import cryptoBalancer.Models.Entities.*;
import cryptoBalancer.Models.TCP.Request;
import cryptoBalancer.Models.TCP.Response;
import cryptoBalancer.Utility.AlertUtil;
import cryptoBalancer.Utility.ClientSocket;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainLayout implements Initializable {

    @FXML
    private AnchorPane centerContent;

    @FXML
    private Label headerLabel;

    // Панели для вкладок
    @FXML
    private AnchorPane newsPane;
    @FXML
    private AnchorPane personalPane;
    @FXML
    private AnchorPane portfolioPane;
    @FXML
    private AnchorPane cryptoDataPane;
    @FXML
    private AnchorPane adminPane;

    // Элементы для новостей
    @FXML
    private ListView<News> newsList;

    // Кнопки боковой панели
    @FXML
    private Button btnNews;
    @FXML
    private Button btnPersonal;
    @FXML
    private Button btnPortfolio;
    @FXML
    private Button btnCryptoData;
    @FXML
    private Button btnAnalysis;
    @FXML
    private Button btnAdmin;

    // Элементы для данных криптовалюты
    @FXML
    private ComboBox<String> cryptoComboBox;
    @FXML
    private LineChart<Number, Number> priceChart;
    @FXML
    private Label cryptoInfoLabel;

    // Элементы для вкладки "Личный кабинет"
    @FXML
    private ImageView avatar;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label portfolioCountLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button savePortfolioButton;

    // Элементы для вкладки "Портфели и анализ"
    @FXML
    private ComboBox<String> portfolioComboBox;
    @FXML
    private Button addPortfolioButton;
    @FXML
    private Button deletePortfolioButton;
    @FXML
    private Button analyzePortfolioButton;
    @FXML
    private ListView<Crypto> cryptoListView;
    @FXML
    private Button addCryptoButton;
    @FXML
    private LineChart<Number, Number> portfolioChart;
    @FXML
    private Label optimizationLabel;
    @FXML
    private ComboBox<String> cryptoPortfolioComboBox;

    // Элементы интерфейса для администрирования
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @FXML private TableView<Crypto> cryptosTable;
    @FXML private TableColumn<Crypto, String> cryptoNameColumn;
    @FXML private TableColumn<Crypto, String> cryptoSymbolColumn;
    @FXML private TableColumn<Crypto, Double> cryptoPriceColumn;
    @FXML private TableColumn<Crypto, String> cryptoDateColumn;

    @FXML private TextField searchUserField;
    @FXML private TextField searchCryptoField;
    @FXML private ToggleGroup roleFilterGroup; // Оставляем поле, но инициализируем вручную
    @FXML private RadioButton filterAll;
    @FXML private RadioButton filterAdmins;
    @FXML private RadioButton filterUsers;
    @FXML private Button deleteUserButton;
    @FXML private Button makeAdminButton;
    @FXML private Button editUserButton;


    private User loggedInUser;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private boolean isPortfolioModified = false;
    private String unsavedPortfolioName;
    private ObservableList<String> portfolioNames = FXCollections.observableArrayList();
    private ObservableList<String> cryptoNames = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Crypto> cryptos = FXCollections.observableArrayList();
    private Portfolio currentPortfolio;
    private boolean isCreatingNewPortfolio = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUserInterface();
        loadCryptoList();
        loadPortfolioList();
        switchToNews();
        initializeAdmin();
        newsList.setCellFactory(listView -> new NewsListCell());
    }

    private void loadNews(){
        try {
            // Отправляем запрос на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_CRYPTO_NEWS)));
            ClientSocket.getInstance().getOut().flush();

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseMessage, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                Type newsListType = new TypeToken<List<News>>() {}.getType();
                List<News> newsItems = gson.fromJson(response.getResponseData(), newsListType);

                newsList.setItems(FXCollections.observableArrayList(newsItems));
            } else {
                // Обработка ошибки
                AlertUtil.showError("Ошибка", "Не удалось загрузить новости", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Не удалось загрузить новости", e.getMessage());
        }
    }

    @FXML
    private void analyzePortfolio(){

    }

    @FXML
    private void deletePortfolio(){
        String selectedPortfolio = portfolioComboBox.getValue();
        if (selectedPortfolio == null) {
            AlertUtil.showWarning("Ошибка", "Выберите портфель для удаления", "");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Удаление портфеля",
                "Вы уверены, что хотите удалить портфель '" + selectedPortfolio + "'?", "");
        if (!confirm) return;

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.DELETE_PORTFOLIO, selectedPortfolio)));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                AlertUtil.showInfo("Успех", "Портфель удалён", "");
                currentPortfolio = null;
                loadPortfolioList();
                cryptoListView.setItems(FXCollections.observableArrayList());
                deletePortfolioButton.setDisable(true);
                analyzePortfolioButton.setDisable(true);
            } else {
                AlertUtil.showError("Ошибка", "Не удалось удалить портфель", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Не удалось удалить портфель", e.getMessage());
        }
    }

    @FXML
    private void addCryptoToPortfolio(){
        String selectedCryptoName = cryptoPortfolioComboBox.getValue();
        if (selectedCryptoName == null) {
            AlertUtil.showWarning("Ошибка", "Выберите криптовалюту для добавления", "");
            return;
        }

        if (currentPortfolio == null) {
            AlertUtil.showWarning("Ошибка", "Выберите или создайте портфель", "");
            return;
        }

        if (currentPortfolio.getInvestments() != null && currentPortfolio.getInvestments().stream()
                .anyMatch(inv -> inv.getCrypto().getName().equals(selectedCryptoName))) {
            AlertUtil.showWarning("Ошибка", "Эта криптовалюта уже добавлена в портфель", "");
            return;
        }

        Crypto newCrypto = new Crypto();
        newCrypto.setName(selectedCryptoName);

        Investment investment = new Investment();
        investment.setPortfolio(currentPortfolio);
        investment.setCrypto(newCrypto);
        investment.setAmount(BigDecimal.ZERO);
        investment.setPurchasePrice(BigDecimal.ZERO);

        if (currentPortfolio.getInvestments() == null) {
            currentPortfolio.setInvestments(new HashSet<>());
        }
        currentPortfolio.getInvestments().add(investment);

        updatePortfolioUI(currentPortfolio);
        isPortfolioModified = true;
        savePortfolioButton.setDisable(false);
        addCryptoButton.setDisable(true);
    }

    @FXML
    private void selectPortfolio(){
        String selectedPortfolio = portfolioComboBox.getValue();
        if (selectedPortfolio == null) {
            deletePortfolioButton.setDisable(true);
            analyzePortfolioButton.setDisable(true);
            return;
        }

        if (isPortfolioModified) {
            boolean save = AlertUtil.showConfirmation("Сохранить изменения",
                    "У вас есть несохранённые изменения. Сохранить перед переключением?", "");
            if (save) {
                savePortfolio();
            } else {
                if (unsavedPortfolioName != null) {
                    portfolioComboBox.getItems().remove(unsavedPortfolioName);
                    if (selectedPortfolio.equals(unsavedPortfolioName)) {
                        portfolioComboBox.setValue(null);
                        unsavedPortfolioName = null;
                        isPortfolioModified = false;
                        savePortfolioButton.setDisable(true);
                        deletePortfolioButton.setDisable(true);
                        analyzePortfolioButton.setDisable(true);
                        return;
                    }
                    unsavedPortfolioName = null;
                }
                isPortfolioModified = false;
                savePortfolioButton.setDisable(true);
            }
        }
        if(isCreatingNewPortfolio){
            loadPortfolioData(selectedPortfolio);
        }
        deletePortfolioButton.setDisable(false);
        analyzePortfolioButton.setDisable(false);
    }

    private void loadPortfolioList(){
        try{
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_PORTFOLIO_LIST, gson.toJson(loggedInUser.getUserId()))));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if(response.getResponseStatus() == ResponseStatus.OK) {
                String[] portfolioNamesArray = gson.fromJson(response.getResponseData(), String[].class);
                portfolioNames.setAll(Arrays.asList(portfolioNamesArray));
                portfolioComboBox.setItems(portfolioNames);
            }
            else{
                AlertUtil.showWarning("Ошибка", "Не удалось загрузить список портфелей", response.getResponseMessage());
            }

        }catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showWarning("Ошибка", "Не удалось загрузить список портфелей", e.getMessage());
        }

    }

    @FXML
    private void savePortfolio(){
        if (isPortfolioModified && unsavedPortfolioName != null) {
            try {
                Portfolio newPortfolio = new Portfolio();
                newPortfolio.setPortfolioName(unsavedPortfolioName);
                newPortfolio.setUser(loggedInUser);
                newPortfolio.setCreatedAt(LocalDateTime.now());

                ObservableList<Crypto> cryptoItems = cryptoListView.getItems();
                List<Investment> investments = new ArrayList<>();
                for (Crypto crypto : cryptoItems) {
                    Investment investment = new Investment();
                    investment.setPortfolio(newPortfolio);
                    investment.setCrypto(crypto);
                    investment.setPurchasePrice(BigDecimal.ZERO);
                    investment.setAmount(BigDecimal.ZERO);
                    investments.add(investment);
                }

                ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.SAVE_PORTFOLIO, gson.toJson(newPortfolio))));
                ClientSocket.getInstance().getOut().flush();

                String responseJson = ClientSocket.getInstance().getInStream().readLine();
                Response response = gson.fromJson(responseJson, Response.class);

                if (response.getResponseStatus() != ResponseStatus.OK) {
                    AlertUtil.showError("Ошибка", "Не удалось сохранить портфель " + unsavedPortfolioName, response.getResponseMessage());
                    return;
                }

                AlertUtil.showInfo("Успех", "Портфель сохранён", "");
                isPortfolioModified = false;
                unsavedPortfolioName = null;
                savePortfolioButton.setDisable(true);
                loadPortfolioList();
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtil.showError("Ошибка", "Не удалось сохранить портфель " + unsavedPortfolioName, e.getMessage());
            }
        }
    }

    private void loadPortfolioData(String portfolioName){
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_PORTFOLIO_DATA, gson.toJson(portfolioName))));
            ClientSocket.getInstance().getOut().flush();

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseMessage, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                currentPortfolio = gson.fromJson(response.getResponseData(), Portfolio.class);
                if (currentPortfolio != null) {
                    updatePortfolioUI(currentPortfolio);
                } else {
                    AlertUtil.showError("Ошибка", "Данные портфеля пусты", "");
                    cryptoPortfolioComboBox.setItems(cryptoNames);
                }
            } else {
                AlertUtil.showError("Ошибка", "Не удалось загрузить данные портфеля", response.getResponseMessage());
                cryptoPortfolioComboBox.setItems(cryptoNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Не удалось загрузить данные портфеля", e.getMessage());
            cryptoPortfolioComboBox.setItems(cryptoNames);
        }
    }

    private void updatePortfolioUI(Portfolio portfolio) {
        if (portfolio == null) {
            cryptoListView.setItems(FXCollections.observableArrayList());
            cryptoPortfolioComboBox.setItems(cryptoNames != null ? cryptoNames : FXCollections.observableArrayList());
            addCryptoButton.setDisable(true);
            portfolioChart.getData().clear(); // Очистка графика
            optimizationLabel.setText("Анализ не выполнен"); // Базовая инициализация
            return;
        }

        Set<Investment> investments = portfolio.getInvestments();
        if (investments != null && !investments.isEmpty()) {
            List<Crypto> cryptos = investments.stream()
                    .map(Investment::getCrypto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            cryptoListView.setItems(FXCollections.observableArrayList(cryptos));

            // Обновляем cryptoPortfolioComboBox, исключая криптовалюты, уже присутствующие в портфеле
            List<String> existingCryptoNames = investments.stream()
                    .map(inv -> inv.getCrypto().getName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            ObservableList<String> availableCryptos = cryptoNames != null ?
                    FXCollections.observableArrayList(
                            cryptoNames.stream()
                                    .filter(name -> name != null && !existingCryptoNames.contains(name))
                                    .collect(Collectors.toList())
                    ) : FXCollections.observableArrayList();
            cryptoPortfolioComboBox.setItems(availableCryptos);

            // Управление кнопкой добавления
            boolean hasAvailableCryptos = !availableCryptos.isEmpty();
            addCryptoButton.setDisable(!hasAvailableCryptos);

        } else {
            cryptoListView.setItems(FXCollections.observableArrayList());
            cryptoPortfolioComboBox.setItems(cryptoNames != null ? cryptoNames : FXCollections.observableArrayList());
            addCryptoButton.setDisable(true);
            portfolioChart.getData().clear(); // Очистка графика
            optimizationLabel.setText("Анализ не выполнен"); // Базовая инициализация
        }
    }

    @FXML
    private void addPortfolio(){
        String portfolioName = AlertUtil.showInputDialog("Добавление портфеля", "Введите название портфеля:", "");
        if (portfolioName != null && !portfolioName.trim().isEmpty()) {
            if (portfolioNames.contains(portfolioName)) {
                AlertUtil.showError("Ошибка", "Портфель с таким названием уже существует", "");
                return;
            }

            // Создаём новый портфель
            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setPortfolioName(portfolioName);
            newPortfolio.setUser(loggedInUser);
            newPortfolio.setCreatedAt(LocalDateTime.now());
            newPortfolio.setInvestments(new HashSet<>()); // Инициализируем список инвестиций

            // Сохраняем портфель как текущий
            currentPortfolio = newPortfolio;
            portfolioComboBox.setItems(portfolioNames);
            portfolioComboBox.setValue(portfolioName);
            unsavedPortfolioName = portfolioName;
            isPortfolioModified = true;
            isCreatingNewPortfolio = true;
            savePortfolioButton.setDisable(false);

            // Очищаем UI и инициализируем аналитику
            cryptoListView.setItems(FXCollections.observableArrayList());
            portfolioChart.getData().clear(); // Очищаем график
            optimizationLabel.setText("Анализ не выполнен"); // Начальное состояние аналитики
            cryptoPortfolioComboBox.setItems(cryptoNames); // Показываем все криптовалюты для добавления
        } else if (portfolioName != null) {
            AlertUtil.showError("Ошибка", "Название портфеля не может быть пустым", "");
        }
    }

    @FXML
    private void changePassword() {
        if (loggedInUser != null) {
            // Простая реализация диалога смены пароля
            String newPassword = AlertUtil.showPasswordDialog("Смена пароля", "Введите новый пароль:");
            if (newPassword != null && !newPassword.isEmpty()) {
                try {
                    User changeUser = new User();
                    changeUser.setUserId(loggedInUser.getUserId());
                    changeUser.setUsername(loggedInUser.getUsername());
                    changeUser.setPasswordHash(newPassword);
                    ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.CHANGE_PASSWORD, gson.toJson(changeUser))));
                    ClientSocket.getInstance().getOut().flush();

                    String responseJson = ClientSocket.getInstance().getInStream().readLine();
                    Response response = gson.fromJson(responseJson, Response.class);

                    if (response.getResponseStatus() == ResponseStatus.OK) {
                        AlertUtil.showInfo("Успех", "Пароль успешно изменён.", response.getResponseMessage());
                    } else {
                        AlertUtil.showError("Ошибка", "Не удалось изменить пароль", response.getResponseMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertUtil.showError("Ошибка", "Не удалось изменить пароль", e.getMessage());
                }
            }
        } else {
            AlertUtil.showError("Ошибка", "Пользователь не авторизован", "");
        }
    }

    @FXML
    public void createReport(){

    }

    @FXML
    private void selectCrypto(){
        String selectedCrypto = cryptoComboBox.getValue();
        if (selectedCrypto != null) {
            loadCryptoData(selectedCrypto);
        }
    }

    @FXML void selectCryptoInPotrfolio(){
        String selectedCrypto = cryptoPortfolioComboBox.getValue();
        addCryptoButton.setDisable(selectedCrypto == null);
    }

    private void loadCryptoList() {
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_CRYPTO_LIST)));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                String[] cryptoNamesArray = gson.fromJson(response.getResponseData(), String[].class);
                cryptoNames.setAll(Arrays.asList(cryptoNamesArray));
                cryptoComboBox.setItems(cryptoNames); // Обновляем комбо-бокс
            } else {
                AlertUtil.showWarning("Ошибка", "Не удалось загрузить список криптовалют", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showWarning("Ошибка", "Не удалось загрузить список криптовалют", e.getMessage());
        }
    }

    private void updateChartAndInfoWithHistory(String cryptoName, List<CryptoHistory> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            cryptoInfoLabel.setText("Нет исторических данных для " + cryptoName);
            priceChart.getData().clear();
            return;
        }

        priceChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(cryptoName);

        // Добавляем все точки данных для построения ломаной линии
        int totalPoints = historyList.size();
        for (int i = 0; i < totalPoints; i++) {
            CryptoHistory entry = historyList.get(i);
            double price = entry.getPrice().doubleValue();
            series.getData().add(new XYChart.Data<>(i, price));
        }

        // Настраиваем ось X: ровно 10 меток с датами
        NumberAxis xAxis = (NumberAxis) priceChart.getXAxis();
        int step = Math.max(1, (totalPoints - 1) / 9); // Делим на 9, чтобы получить 10 точек (0, step, 2*step, ..., 9*step)

        // Создаём список индексов для 10 меток
        List<Integer> labelIndices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = Math.min(i * step, totalPoints - 1); // Не превышаем размер списка
            labelIndices.add(index);
        }

        // Форматируем метки
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                int index = value.intValue();
                if (labelIndices.contains(index)) {
                    return historyList.get(index).getDateChanged().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null; // Не используется
            }
        });

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(totalPoints - 1);
        xAxis.setTickUnit(step);
        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false); // Отключаем автонастройку, чтобы контролировать метки

        priceChart.getData().add(series);

        // Убираем точки на графике
        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 2px;");
        series.getData().forEach(data -> {
            data.getNode().setVisible(false); // Скрываем точки
            data.getNode().setManaged(false);
        });

        // Обновляем метку с информацией
        BigDecimal latestPrice = historyList.get(historyList.size() - 1).getPrice();
        String infoText = String.format("Криптовалюта: %s\nПоследняя цена: %.2f USD", cryptoName, latestPrice);
        cryptoInfoLabel.setText(infoText);
    }

    private void loadCryptoData(String cryptoName) {
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_CRYPTO_DATA, cryptoName)));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                // Десериализуем список истории
                Type historyType = new TypeToken<List<CryptoHistory>>(){}.getType();
                List<CryptoHistory> historyList = gson.fromJson(response.getResponseData(), historyType);

                if (historyList != null && !historyList.isEmpty()) {
                    updateChartAndInfoWithHistory(cryptoName, historyList);
                } else {
                    AlertUtil.showError("Ошибка", "История для криптовалюты не найдена", "");
                }
            } else {
                AlertUtil.showError("Ошибка", "Не удалось загрузить данные криптовалюты", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Не удалось загрузить данные криптовалюты", e.getMessage());
        }
    }

    private void updateChartAndInfoWithCrypto(Crypto crypto) {
        if (crypto == null) {
            cryptoInfoLabel.setText("Данные недоступны.");
            priceChart.getData().clear();
            return;
        }

        String cryptoName = crypto.getName();
        List<CryptoHistory> history = crypto.getHistory();
        if (history == null || history.isEmpty()) {
            cryptoInfoLabel.setText("Нет исторических данных для " + cryptoName);
            priceChart.getData().clear();
            return;
        }

        priceChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(cryptoName);

        // Добавляем все точки данных для построения ломаной линии
        int totalPoints = history.size();
        for (int i = 0; i < totalPoints; i++) {
            CryptoHistory entry = history.get(i);
            double price = entry.getPrice().doubleValue();
            series.getData().add(new XYChart.Data<>(i, price));
        }

        // Настраиваем ось X: ровно 10 меток с датами
        NumberAxis xAxis = (NumberAxis) priceChart.getXAxis();
        int step = Math.max(1, (totalPoints - 1) / 9); // Делим на 9, чтобы получить 10 точек

        // Создаём список индексов для 10 меток
        List<Integer> labelIndices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = Math.min(i * step, totalPoints - 1);
            labelIndices.add(index);
        }

        // Форматируем метки
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                int index = value.intValue();
                if (labelIndices.contains(index)) {
                    return history.get(index).getDateChanged().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null; // Не используется
            }
        });

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(totalPoints - 1);
        xAxis.setTickUnit(step);
        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);

        priceChart.getData().add(series);

        // Убираем точки на графике
        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 2px;");
        series.getData().forEach(data -> {
            data.getNode().setVisible(false); // Скрываем точки
            data.getNode().setManaged(false);
        });

        // Обновляем метку с информацией
        BigDecimal latestPrice = history.get(history.size() - 1).getPrice();
        String infoText = String.format("Криптовалюта: %s\nПоследняя цена: %.2f USD", cryptoName, latestPrice);
        cryptoInfoLabel.setText(infoText);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    private void updateUserInterface() {
        if (loggedInUser != null) {
            // Устанавливаем имя пользователя
            headerLabel.setText("ДОБРО ПОЖАЛОВАТЬ, " + loggedInUser.getUsername().toUpperCase());
            // Проверяем роль пользователя
            if ("Администратор".equals(loggedInUser.getRole().getRoleName()))
                btnAdmin.setVisible(true);
            else
                btnAdmin.setVisible(false);
        } else {
            headerLabel.setText("ДОБРО ПОЖАЛОВАТЬ");
            btnAdmin.setVisible(false);
        }
    }

    private void loadAllCrypto(){

    }

    @FXML
    private void switchToNews() {
        showPane(newsPane);
        headerLabel.setText("Новости");
        loadNews();
    }

    @FXML
    private void switchToPersonal() {
        showPane(personalPane);
        headerLabel.setText("Личный кабинет");
        roleLabel.setText("Роль: " + getUserRole());
        usernameLabel.setText(loggedInUser.getUsername());
        emailLabel.setText("Email: " + maskEmail(loggedInUser.getEmail()));
        portfolioCountLabel.setText("Количество портфелей: " + portfolioComboBox.getItems().size());
    }

    @FXML
    private void switchToPortfolio() {
        showPane(portfolioPane);
        headerLabel.setText("Управление портфелем");
        ObservableList<String> observablePortfolioNames = FXCollections.observableArrayList(portfolioNames);
        portfolioComboBox.setItems(observablePortfolioNames);
    }

    @FXML
    private void switchToCryptoData() {
        showPane(cryptoDataPane);
        headerLabel.setText("Данные криптовалюты");
    }


    @FXML
    private void switchToAdmin() {
        showPane(adminPane);
        headerLabel.setText("Техническое управление");
        loadAdminData();
    }


    private void initializeAdmin() {
        roleFilterGroup = new ToggleGroup();
        filterAll.setToggleGroup(roleFilterGroup);
        filterAdmins.setToggleGroup(roleFilterGroup);
        filterUsers.setToggleGroup(roleFilterGroup);

        // Настройка таблиц
        setupUsersTable();
        setupCryptosTable();

        // Слушатели для поиска
        searchUserField.textProperty().addListener((obs, oldValue, newValue) -> searchAndFilterUsers());
        searchCryptoField.textProperty().addListener((obs, oldValue, newValue) -> searchAndFilterCryptos());

        // Слушатель для фильтра ролей
        if (roleFilterGroup != null) {
            roleFilterGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> searchAndFilterUsers());
        } else {
            System.err.println("roleFilterGroup is null. Check FXML configuration.");
        }

        // Отключение кнопок, если пользователь не выбран
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            deleteUserButton.setDisable(!isSelected);
            makeAdminButton.setDisable(!isSelected);
            editUserButton.setDisable(!isSelected);
        });
    }

    private void setupUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().getRoleName()));

        usersTable.setItems(users);
    }

    private void setupCryptosTable() {
        cryptoNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cryptoSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        // Извлекаем последнюю цену из истории
        cryptoPriceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Crypto, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Crypto, Double> cellData) {
                Crypto crypto = cellData.getValue();
                List<CryptoHistory> history = crypto.getHistory();
                if (history != null && !history.isEmpty()) {
                    BigDecimal latestPrice = history.get(history.size() - 1).getPrice();
                    return new SimpleDoubleProperty(latestPrice.doubleValue()).asObject();
                }
                return new SimpleDoubleProperty(0.0).asObject();
            }
        });

        // Извлекаем последнюю дату из истории
        cryptoDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Crypto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Crypto, String> cellData) {
                Crypto crypto = cellData.getValue();
                List<CryptoHistory> history = crypto.getHistory();
                if (history != null && !history.isEmpty()) {
                    LocalDate latestDate = history.get(history.size() - 1).getDateChanged();
                    return new SimpleStringProperty(latestDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
                return new SimpleStringProperty("N/A");
            }
        });

        cryptosTable.setItems(cryptos);
    }

    private void loadAdminData() {
        loadAllCryptos();
        loadAllUsers();
    }

    private void loadAllUsers() {
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_ALL_USERS)));
            ClientSocket.getInstance().getOut().flush();

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseMessage, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                Type userListType = new TypeToken<List<User>>() {}.getType();
                List<User> userList = gson.fromJson(response.getResponseData(), userListType);
                users.setAll(userList);
            } else {
                AlertUtil.showError("Ошибка сервера", "Не удалось загрузить пользователей", response.getResponseMessage());
            }
        } catch (IOException e) {
            AlertUtil.showError("Ошибка соединения", "Не удалось связаться с сервером", "Проверьте подключение: " + e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Неизвестная ошибка", "Произошла ошибка при загрузке пользователей", e.getMessage());
        }
    }

    private void loadAllCryptos() {
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.GET_ALL_CRYPTO)));
            ClientSocket.getInstance().getOut().flush();

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseMessage, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                // Десериализуем список криптовалют из responseData
                Type cryptoListType = new TypeToken<List<Crypto>>() {}.getType();
                List<Crypto> cryptoList = gson.fromJson(response.getResponseData(), cryptoListType);
                cryptos.setAll(cryptoList); // Обновляем ObservableList для таблицы криптовалют
            } else {
                AlertUtil.showError("Ошибка", "Не удалось загрузить список криптовалют", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Не удалось загрузить список криптовалют", e.getMessage());
        }
    }

    private void searchAndFilterUsers() {
        String searchText = searchUserField.getText().toLowerCase();
        RadioButton selectedFilter = (RadioButton) roleFilterGroup.getSelectedToggle();
        String roleFilter = selectedFilter.getText();

        ObservableList<User> filteredUsers = FXCollections.observableArrayList(
                users.stream()
                        .filter(user -> user.getUsername().toLowerCase().contains(searchText) ||
                                user.getEmail().toLowerCase().contains(searchText))
                        .filter(user -> roleFilter.equals("Все") || user.getRole().getRoleName().equals(roleFilter))
                        .collect(Collectors.toList())
        );
        usersTable.setItems(filteredUsers);
    }

    private void searchAndFilterCryptos() {
        String searchText = searchCryptoField.getText().toLowerCase();
        ObservableList<Crypto> filteredCryptos = FXCollections.observableArrayList(
                cryptos.stream()
                        .filter(crypto -> crypto.getName().toLowerCase().contains(searchText) ||
                                crypto.getSymbol().toLowerCase().contains(searchText))
                        .collect(Collectors.toList())
        );
        cryptosTable.setItems(filteredCryptos);
    }

    @FXML
    private void deleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Предупреждение", "Выберите пользователя для удаления", "");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Удаление пользователя",
                "Вы уверены, что хотите удалить пользователя '" + selectedUser.getUsername() + "'?", "");
        if (!confirm) return;

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.DELETE_USER, gson.toJson(selectedUser.getUserId()))));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                users.remove(selectedUser);
                AlertUtil.showInfo("Успех", "Пользователь удалён", "");
            } else {
                AlertUtil.showError("Ошибка", "Не удалось удалить пользователя", response.getResponseMessage());
            }
        } catch (IOException e) {
            AlertUtil.showError("Ошибка", "Не удалось удалить пользователя", e.getMessage());
        }
    }

    @FXML
    private void makeAdmin() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Предупреждение", "Выберите пользователя для назначения администратором", "");
            return;
        }

        if ("Администратор".equals(selectedUser.getRole().getRoleName())) {
            AlertUtil.showWarning("Предупреждение", "Пользователь уже является администратором", "");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Назначение администратора",
                "Назначить '" + selectedUser.getUsername() + "' администратором?", "");
        if (!confirm) return;

        try {
            Role newRole = new Role();
            newRole.setRoleName("Администратор");
            selectedUser.setRole(newRole);

            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.UPDATE_USER_ROLE, gson.toJson(selectedUser))));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                AlertUtil.showInfo("Успех", "Роль пользователя обновлена", "");
                loadAllUsers(); // Обновляем таблицу
            } else {
                AlertUtil.showError("Ошибка", "Не удалось обновить роль", response.getResponseMessage());
            }
        } catch (IOException e) {
            AlertUtil.showError("Ошибка", "Не удалось обновить роль", e.getMessage());
        }
    }

    @FXML
    private void editUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Предупреждение", "Выберите пользователя для редактирования", "");
            return;
        }

        String newEmail = AlertUtil.showInputDialog("Редактирование пользователя", "Введите новый email:", selectedUser.getEmail());
        if (newEmail == null || newEmail.isEmpty()) return;

        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.showError("Ошибка", "Некорректный формат email", "");
            return;
        }

        selectedUser.setEmail(newEmail);
        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(new Request(RequestType.UPDATE_USER, gson.toJson(selectedUser))));
            ClientSocket.getInstance().getOut().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                AlertUtil.showInfo("Успех", "Данные пользователя обновлены", "");
                loadAllUsers(); // Обновляем таблицу
            } else {
                AlertUtil.showError("Ошибка", "Не удалось обновить данные", response.getResponseMessage());
            }
        } catch (IOException e) {
            AlertUtil.showError("Ошибка", "Не удалось обновить данные", e.getMessage());
        }
    }

    private void showPane(AnchorPane paneToShow) {
        // Скрываем все панели
        newsPane.setVisible(false);
        personalPane.setVisible(false);
        portfolioPane.setVisible(false);
        cryptoDataPane.setVisible(false);

        adminPane.setVisible(false);

        // Показываем выбранную панель
        paneToShow.setVisible(true);
    }

    private String getUserRole() {
        return loggedInUser.getRole().getRoleName();
    }

    public static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return "***";

        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedName = name.charAt(0) + "*".repeat(Math.max(0, name.length() - 1));
        return maskedName + domain;
    }
}