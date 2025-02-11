package com.alcea.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordEncoder {
    public static final int COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SIZE = 128;
    private static SecureRandom random = new SecureRandom();


    public static String[] hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] res = new String[2];
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        byte[] salt = new byte[SIZE / 8];
        random.nextBytes(salt);
        byte[] dk = pbkdf2(password.toCharArray(), salt);
        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        res[0] = enc.encodeToString(hash);
        res[1] = enc.encodeToString(salt);
        return res;
    }
    public static boolean authenticate(String password, String hashStored, String saltStored) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] hash = Base64.getUrlDecoder().decode(hashStored);
        byte[] salt = Base64.getUrlDecoder().decode(saltStored);
        byte[] check = pbkdf2(password.toCharArray(), salt);
        int zero = 0;
        for (int idx = 0; idx < check.length; ++idx)
            zero |= hash[salt.length + idx] ^ check[idx];
        return zero == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 1 << COST, SIZE);
        SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }
}
