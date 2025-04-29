    package cryptoBalancer;

    import cryptoBalancer.Utility.ClientSocket;
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Stage;

    import java.io.IOException;

    public class Main extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            ClientSocket.getInstance().getSocket();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/cryptoBalancer/Login.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("CryptoBalancer");
            stage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
