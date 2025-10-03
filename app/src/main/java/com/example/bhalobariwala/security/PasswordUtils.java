// app/src/main/java/com/example/bhalobariwala/security/PasswordUtils.java
package com.example.bhalobariwala.security;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

public class PasswordUtils {
    private static final int SALT_LEN = 16;

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hash(String password, String b64Salt) {
        try {
            byte[] salt = Base64.decode(b64Salt, Base64.NO_WRAP);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}
