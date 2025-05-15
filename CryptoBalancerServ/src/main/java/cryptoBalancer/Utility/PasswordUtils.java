package cryptoBalancer.Utility;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // Хэширование пароля
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 - степень "дороговизны" (чем выше, тем медленнее)
    }

    // Проверка пароля
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
