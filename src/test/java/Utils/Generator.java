package Utils;

import java.util.Random;

public class Generator {
    public static String generateRandomToken() {
        String chars = "ABCDEF0123456789";
        StringBuilder sb = new StringBuilder(32);
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
