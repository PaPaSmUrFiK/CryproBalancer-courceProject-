package cryptoBalancer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainLayout {

    @FXML
    private AnchorPane centerContent;

    @FXML
    private Label headerLabel;

    @FXML
    private ListView<String> newsList; // Ссылка на ListView для новостей

    @FXML
    private Button btnPersonal;

    @FXML
    private Button btnPortfolio;

    @FXML
    private Button btnCryptoData;

    @FXML
    private Button btnAnalysis;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnNews;

    @FXML
    private Button btnAdmin;

    @FXML
    public void initialize() {
        String userRole = getUserRole();
        if ("technical_specialist".equals(userRole)) {
            btnAdmin.setVisible(true);
        }

        // Установка имени пользователя (заглушка, замените на реальную логику)
        String username = "User123"; // Пример, можно взять из базы данных (appuser.username)
        headerLabel.setText("ДОБРО ПОЖАЛОВАТЬ, " + username);

        // Изначально отображаем новости
        switchToNews();
    }

    @FXML
    private void switchToPersonal() {
        AnchorPane personalPane = new AnchorPane();
        personalPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Личный кабинет: Просмотр профиля");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        personalPane.getChildren().add(label);
        centerContent.getChildren().setAll(personalPane);
        headerLabel.setText("Личный кабинет");
    }

    @FXML
    private void switchToPortfolio() {
        AnchorPane portfolioPane = new AnchorPane();
        portfolioPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Управление портфелем: Создание/загрузка портфеля");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        portfolioPane.getChildren().add(label);
        centerContent.getChildren().setAll(portfolioPane);
        headerLabel.setText("Управление портфелем");
    }

    @FXML
    private void switchToCryptoData() {
        AnchorPane cryptoDataPane = new AnchorPane();
        cryptoDataPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Данные криптовалюты: График цен и информация");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        cryptoDataPane.getChildren().add(label);
        centerContent.getChildren().setAll(cryptoDataPane);
        headerLabel.setText("Данные криптовалюты");
    }

    @FXML
    private void switchToAnalysis() {
        AnchorPane analysisPane = new AnchorPane();
        analysisPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Анализ и оптимизация: Расчеты и эффективная граница");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        analysisPane.getChildren().add(label);
        centerContent.getChildren().setAll(analysisPane);
        headerLabel.setText("Анализ и оптимизация");
    }

    @FXML
    private void switchToReports() {
        AnchorPane reportsPane = new AnchorPane();
        reportsPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Отчеты: Просмотр и экспорт");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        reportsPane.getChildren().add(label);
        centerContent.getChildren().setAll(reportsPane);
        headerLabel.setText("Отчеты");
    }

    @FXML
    private void switchToNews() {
        AnchorPane newsPane = new AnchorPane();
        newsPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        // Пример списка новостей (заглушка)
        ListView<String> newsList = new ListView<>();
        ObservableList<String> newsItems = FXCollections.observableArrayList(
                "15.05.2025: Bitcoin достигает нового максимума!",
                "14.05.2025: Ethereum обновляет протокол",
                "13.05.2025: Рынок стабилизируется после падения"
        );
        newsList.setItems(newsItems);
        AnchorPane.setTopAnchor(newsList, 20.0);
        AnchorPane.setLeftAnchor(newsList, 20.0);
        AnchorPane.setRightAnchor(newsList, 20.0);
        AnchorPane.setBottomAnchor(newsList, 20.0);

        newsPane.getChildren().add(newsList);
        centerContent.getChildren().setAll(newsPane);
        headerLabel.setText("Новости");
    }

    @FXML
    private void switchToAdmin() {
        AnchorPane adminPane = new AnchorPane();
        adminPane.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        Label label = new Label("Техническое управление: Управление пользователями и криптовалютами");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 20.0);
        adminPane.getChildren().add(label);
        centerContent.getChildren().setAll(adminPane);
        headerLabel.setText("Техническое управление");
    }

    private String getUserRole() {
        return "investor"; // Заглушка
    }
}