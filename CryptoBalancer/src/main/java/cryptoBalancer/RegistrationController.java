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
import cryptoBalancer.Strategy.RegistrationValidationStrategy;
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

public class RegistrationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registrationButton;

    public void registerPressed(ActionEvent actionEvent) {
        String username = loginField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();

        // Используем стратегию для валидации
        ValidationStrategy strategy = new RegistrationValidationStrategy(username, password, confirmPassword, email);
        ValidationResult result = strategy.validate();

        // Отображаем ошибки через AlertUtil
        if (!result.isValid()) {
            AlertUtil.showValidationErrors(result);
            return;
        }

        // Создание объекта пользователя
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password); // Хэширование будет на сервере
        user.setEmail(email);

        // Отправка запроса на сервер
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            Request request = new Request();
            request.setRequestMessage(gson.toJson(user));
            request.setRequestType(RequestType.REGISTER);

            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response response = gson.fromJson(answer, Response.class);

            if (response.getResponseStatus() == ResponseStatus.OK) {
                AlertUtil.showInfo("Успех", "Регистрация завершена", "Вы успешно зарегистрированы!");
                registrationButton.getScene().getWindow().hide();

                // Переход на форму логина
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cryptoBalancer/Login.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                AlertUtil.showError("Ошибка регистрации", "Не удалось зарегистрироваться", response.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Ошибка", "Проблема с сервером", "Не удалось подключиться к серверу.");
        }
    }

    public void loginPressed(ActionEvent actionEvent) {
        loginButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cryptoBalancer/Login.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
}