package cryptoBalancer.Strategy;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final boolean isValid;
    private final List<String> errorMessages;

    public ValidationResult(boolean isValid) {
        this.isValid = isValid;
        this.errorMessages = new ArrayList<>();
    }

    public ValidationResult(boolean isValid, List<String> errorMessages) {
        this.isValid = isValid;
        this.errorMessages = errorMessages != null ? errorMessages : new ArrayList<>();
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void addError(String error) {
        errorMessages.add(error);
    }
}