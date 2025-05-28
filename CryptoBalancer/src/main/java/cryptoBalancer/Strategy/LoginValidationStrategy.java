package cryptoBalancer.Strategy;

import java.util.ArrayList;
import java.util.List;

public class LoginValidationStrategy implements ValidationStrategy{
    private final String username;
    private final String password;

    public LoginValidationStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public ValidationResult validate() {
        List<String> errors = new ArrayList<>();

        if (username == null || username.isEmpty()) errors.add("Логин не может быть пустым.");
        if (password == null || password.isEmpty()) errors.add("Пароль не может быть пустым.");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
