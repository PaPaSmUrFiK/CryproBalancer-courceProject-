package cryptoBalancer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cryptoBalancer.Adapters.LocalDateAdapter;
import cryptoBalancer.Adapters.LocalDateTimeAdapter;
import cryptoBalancer.Enums.RequestType;
import cryptoBalancer.Enums.ResponseStatus;
import cryptoBalancer.Models.Entities.User;
import cryptoBalancer.Models.TCP.Request;
import cryptoBalancer.Models.TCP.Response;
import cryptoBalancer.Strategy.LoginValidationStrategy;
import cryptoBalancer.Strategy.ValidationResult;
import cryptoBalancer.Strategy.ValidationStrategy;
import cryptoBalancer.Utility.AlertUtil;
import cryptoBalancer.Utility.ClientSocket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registrationButton;

    private final Gson gson;

    public LoginController() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public void loginPressed(ActionEvent actionEvent) throws IOException {
        String username = loginField.getText();
        String password = passwordField.getText();

        // Используем стратегию для валидации
        ValidationStrategy strategy = new LoginValidationStrategy(username, password);
        ValidationResult result = strategy.validate();

        // Отображаем ошибки через AlertUtil
        if (!result.isValid()) {
            AlertUtil.showValidationErrors(result);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);

        Request request = new Request();
        request.setRequestMessage(gson.toJson(user));
        request.setRequestType(RequestType.LOGIN);

        ClientSocket.getInstance().getOut().println(gson.toJson(request));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response response = gson.fromJson(answer, Response.class);

        if (response.getResponseStatus() == ResponseStatus.OK) {
            User loggedInUser = gson.fromJson(response.getResponseData(), User.class);
            System.out.println(loggedInUser);
            loginButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cryptoBalancer/MainLayout.fxml"));

            loader.setControllerFactory(controllerClass -> {
                MainLayout mainLayoutController = new MainLayout();
                mainLayoutController.setLoggedInUser(loggedInUser);
                return mainLayoutController;
            });

            Parent root;
            try {
                root = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showError("Ошибка", "Не удалось загрузить интерфейс приложения", e.getMessage());
                return;
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            AlertUtil.showError("Ошибка входа", "Не удалось войти", response.getResponseMessage());
        }
    }

    public void registerPressed(ActionEvent actionEvent) {
        registrationButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cryptoBalancer/Registration.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка загрузки", "Не удалось загрузить форму регистрации", e.getMessage());
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
}