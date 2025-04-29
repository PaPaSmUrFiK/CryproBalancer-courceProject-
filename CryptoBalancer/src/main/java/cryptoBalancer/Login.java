package cryptoBalancer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import com.google.gson.GsonBuilder;
import cryptoBalancer.Adapters.LocalDateAdapter;
import cryptoBalancer.Adapters.LocalDateTimeAdapter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import cryptoBalancer.Enums.RequestType;
import cryptoBalancer.Enums.ResponseStatus;
import cryptoBalancer.Models.Entities.User;
import cryptoBalancer.Models.TCP.Request;

import com.google.gson.Gson;
import cryptoBalancer.Models.TCP.Response;
import cryptoBalancer.Utility.ClientSocket;

public class Login {

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

    public void loginPressed(ActionEvent actionEvent) throws IOException{
        User user = new User();
        user.setUsername(loginField.getText());
        user.setPasswordHash(passwordField.getText());
        Request request = new Request();
        request.setRequestMessage(new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(user));
        request.setRequestType(RequestType.LOGIN);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();
        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if(response.getResponseStatus() == ResponseStatus.OK){
            System.out.println("Вход выполнен");
        }else{
            System.out.println("Неверные данные");
        }
    }

    public void registerPressed(ActionEvent actionEvent){
        registrationButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cryptoBalancer/Registration.fxml"));

        try{
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось загрузить форму регистрации");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

}

