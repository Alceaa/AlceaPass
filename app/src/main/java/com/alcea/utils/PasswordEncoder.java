package com.alcea.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncoder {
    public static final int COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String ALGORITHM_DATA = "AES/CBC/PKCS5Padding";
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

    public static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    public static String encrypt(String password, String masterPassword) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        SecretKeySpec keySpec = new SecretKeySpec(digest.digest(masterPassword.getBytes(StandardCharsets.UTF_8)), "AES");
        byte[] iv = generateIV();
        Cipher cipher = Cipher.getInstance(ALGORITHM_DATA);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(password.getBytes());

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }
    public static String decrypt(String encryptedData, String masterPassword) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        byte[] encrypted = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

        SecretKeySpec keySpec = new SecretKeySpec(masterPassword.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM_DATA);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] original = cipher.doFinal(encrypted);
        return new String(original);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 1 << COST, SIZE);
        SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

}
