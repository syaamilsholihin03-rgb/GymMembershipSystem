package com.gym.gymmembershipsystem.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }


    // =========================================================
    // HASH PASSWORD
    // =========================================================
    public static String hashPassword(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        byte[] salt = generateSalt();

        byte[] hash = generateHash(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        return ITERATIONS
                + ":"
                + Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }


    // =========================================================
    // VERIFY PASSWORD
    // =========================================================
    public static boolean verifyPassword(
            String password,
            String storedPasswordHash) {

        if (password == null
                || storedPasswordHash == null
                || storedPasswordHash.isEmpty()) {

            return false;
        }

        try {

            String[] parts =
                    storedPasswordHash.split(":");

            if (parts.length != 3) {
                return false;
            }

            int iterations =
                    Integer.parseInt(parts[0]);

            byte[] salt =
                    Base64
                            .getDecoder()
                            .decode(parts[1]);

            byte[] storedHash =
                    Base64
                            .getDecoder()
                            .decode(parts[2]);

            byte[] calculatedHash =
                    generateHash(
                            password.toCharArray(),
                            salt,
                            iterations,
                            storedHash.length * 8
                    );

            return slowEquals(
                    storedHash,
                    calculatedHash
            );

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // GENERATE RANDOM SALT
    // =========================================================
    private static byte[] generateSalt() {

        SecureRandom secureRandom =
                new SecureRandom();

        byte[] salt =
                new byte[SALT_LENGTH];

        secureRandom.nextBytes(salt);

        return salt;
    }


    // =========================================================
    // GENERATE PASSWORD HASH
    // =========================================================
    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength) {

        PBEKeySpec spec =
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        keyLength
                );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return factory
                    .generateSecret(spec)
                    .getEncoded();

        } catch (NoSuchAlgorithmException
                 | InvalidKeySpecException e) {

            throw new RuntimeException(
                    "Unable to hash password.",
                    e
            );

        } finally {

            spec.clearPassword();
        }
    }


    // =========================================================
    // SECURE HASH COMPARISON
    // =========================================================
    private static boolean slowEquals(
            byte[] first,
            byte[] second) {

        if (first == null || second == null) {
            return false;
        }

        int difference =
                first.length ^ second.length;

        for (int i = 0;
             i < first.length && i < second.length;
             i++) {

            difference |=
                    first[i] ^ second[i];
        }

        return difference == 0;
    }
}