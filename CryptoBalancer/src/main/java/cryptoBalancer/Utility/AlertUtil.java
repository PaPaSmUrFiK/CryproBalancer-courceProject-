package cryptoBalancer.Utility;

import cryptoBalancer.Strategy.ValidationResult;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlertUtil {
    public static void showInfo(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    public static void showError(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    public static void showWarning(String title, String header, String content) {
        showAlert(AlertType.WARNING, title, header, content);
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static String showPasswordDialog(String title, String header) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Настройка иконки (опционально, если есть иконка приложения)
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        // stage.getIcons().add(new javafx.scene.image.Image("/images/icon.png")); // Укажите путь к иконке, если есть

        // Создаём PasswordField для ввода пароля
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите новый пароль");

        // Создаём PasswordField для подтверждения пароля
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Подтвердите новый пароль");

        // Макет для диалога
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Новый пароль:"), passwordField,
                new Label("Подтверждение пароля:"), confirmPasswordField
        );

        dialog.getDialogPane().setContent(vbox);

        // Добавляем кнопки "OK" и "Cancel"
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Устанавливаем результат диалога
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                // Проверяем, что поля не пустые
                if (password == null || password.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
                    showError("Ошибка", "Поля пароля не могут быть пустыми", "Пожалуйста, заполните оба поля.");
                    return null;
                }

                // Проверяем совпадение паролей
                if (!password.equals(confirmPassword)) {
                    showError("Ошибка", "Пароли не совпадают", "Пожалуйста, убедитесь, что пароли совпадают.");
                    return null;
                }

                if (password.length() < 8) {
                    AlertUtil.showError("Ошибка", "Пароль должен содержать минимум 8 символов", "");
                    return null;
                }

                if (password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
                    AlertUtil.showError("Ошибка", "Пароль должен содержать хотя бы одну букву и одну цифру", "");
                    return null;
                }

                return password;
            }
            return null;
        });

        // Показываем диалог и возвращаем результат
        return dialog.showAndWait().orElse(null);
    }

    public static String showInputDialog(String title, String header, String prompt) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Настройка иконки (опционально, если есть иконка приложения)
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        // stage.getIcons().add(new javafx.scene.image.Image("/images/icon.png")); // Укажите путь к иконке, если есть

        // Создаём TextField для ввода текста
        TextField textField = new TextField();
        textField.setPromptText(prompt);

        // Макет для диалога
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(textField);

        dialog.getDialogPane().setContent(vbox);

        // Добавляем кнопки "OK" и "Cancel"
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Устанавливаем результат диалога
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String input = textField.getText();
                if (input == null || input.trim().isEmpty()) {
                    showError("Ошибка", "Поле не может быть пустым", "Пожалуйста, введите значение.");
                    return null;
                }
                return input;
            }
            return null;
        });

        // Показываем диалог и возвращаем результат
        return dialog.showAndWait().orElse(null);
    }

    public static void showValidationErrors(ValidationResult result) {
        if (!result.isValid()) {
            StringBuilder errorMessage = new StringBuilder("Обнаружены ошибки:\n");
            for (String error : result.getErrorMessages()) {
                errorMessage.append(error).append("\n");
            }
            showError("Ошибка валидации", "Исправьте ошибки", errorMessage.toString());
        }
    }

    private static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}