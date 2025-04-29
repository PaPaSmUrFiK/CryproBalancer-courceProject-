import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Enums.Roles;
import main.Models.Entities.PersonData;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Register  implements Initializable {
    public PasswordField passwordfieldPassword;
    public PasswordField passwordfieldConfirmPassword;
    public Button buttonSignUp;
    public Button buttonBack;
    public TextField textfieldLogin;
    public Label labelMessage;
    public RadioButton cbFemale;
    public RadioButton cbMale;
    public TextField textfieldEmail;
    public Spinner spinnerAge;
    public TextField textfieldAddress;
    public ToggleGroup Sex;
    private SpinnerValueFactory<Integer> valueMarkFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60);

    public void Signup_Pressed(ActionEvent actionEvent) throws IOException {
        labelMessage.setVisible(false);
        User user = new User();
        user.setLogin(textfieldLogin.getText());
        user.setPassword(textfieldLogin.getText());
        user.setRole(Roles.User);
        user.setName(textfieldLogin.getText());
        PersonData personData = new PersonData();
        personData.setAddress(textfieldAddress.getText());
        personData.setAge((Integer) spinnerAge.getValue());
        personData.setMail(textfieldEmail.getText());
        if(cbFemale.isSelected())
            personData.setSex("Ж");
        else
            personData.setSex("M");
        user.setPersonData(personData);
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if (textfieldLogin.equals("") || passwordfieldPassword.equals("") || passwordfieldConfirmPassword.equals("")) {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);
            return;
        }
        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();
        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseData(), User.class));
            Stage stage = (Stage) buttonBack.getScene().getWindow();
            Parent root;
                root = FXMLLoader.load(getClass().getResource("Flights.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
        } else {
            labelMessage.setText("Пользователь с таким логином уже существует.");
            labelMessage.setVisible(true);
        }
    }

    public void Back_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valueMarkFactory.setValue(1);
        spinnerAge.setValueFactory(valueMarkFactory);
    }
}
