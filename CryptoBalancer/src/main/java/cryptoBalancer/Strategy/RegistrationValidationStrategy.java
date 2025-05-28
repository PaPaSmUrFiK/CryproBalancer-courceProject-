package cryptoBalancer.Strategy;

import java.util.ArrayList;
import java.util.List;

public class RegistrationValidationStrategy implements ValidationStrategy {
    private final String username;
    private final String password;
    private final String confirmPassword;
    private final String email;

    public RegistrationValidationStrategy(String username, String password, String confirmPassword, String email) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
    }

    @Override
    public ValidationResult validate() {
        List<String> errors = new ArrayList<>();

        // Проверка на пустые поля
        if (username == null || username.isEmpty()) errors.add("Логин не может быть пустым.");
        if (password == null || password.isEmpty()) errors.add("Пароль не может быть пустым.");
        if (confirmPassword == null || confirmPassword.isEmpty()) errors.add("Подтверждение пароля не может быть пустым.");
        if (email == null || email.isEmpty()) errors.add("Email не может быть пустым.");

        if (!password.equals(confirmPassword)) errors.add("Пароли не совпадают.");

        if (password.length() < 8) errors.add("Пароль должен содержать не менее 8 символов.");

        // Проверка формата email
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) errors.add("Некорректный формат email.");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
