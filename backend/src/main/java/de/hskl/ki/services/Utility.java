package de.hskl.ki.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;

public class Utility {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomString(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase(Locale.ROOT);
        String digits = "0123456789";
        String alphanum = upper + lower + digits;
        char[] symbols = alphanum.toCharArray();
        char[] buffer = new char[length];

        for (int idx = 0; idx < buffer.length; ++idx)
            buffer[idx] = symbols[secureRandom.nextInt(symbols.length)];

        return new String(buffer);
    }
}
