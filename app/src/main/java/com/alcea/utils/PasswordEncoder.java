package com.alcea.utils;

import com.alcea.models.Password;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
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


    public static Password hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Password res = new Password();
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        byte[] salt = new byte[SIZE / 8];
        random.nextBytes(salt);
        byte[] dk = pbkdf2(password.toCharArray(), salt);
        res.hash = enc.encodeToString(dk);
        res.salt = enc.encodeToString(salt);
        return res;
    }
    public static boolean authenticate(String password, String hashStored, String saltStored) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] hash = Base64.getUrlDecoder().decode(hashStored);
        byte[] salt = Base64.getUrlDecoder().decode(saltStored);
        byte[] check = pbkdf2(password.toCharArray(), salt);
        int zero = 0;
        for (int idx = 0; idx < check.length; ++idx)
            zero |= hash[idx] ^ check[idx];
        return zero == 0;
    }

    public static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    public static String encrypt(String password, String masterPassword) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getUrlDecoder().decode(masterPassword), "AES");
        byte[] iv = generateIV();
        Cipher cipher = Cipher.getInstance(ALGORITHM_DATA);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] encryptedPassword = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDataWithIv = new byte[iv.length + encryptedPassword.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedPassword, 0, encryptedDataWithIv, iv.length, encryptedPassword.length);
        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, String masterPassword) throws Exception {
        byte[] encryptedDataWithIv = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        byte[] encryptedPassword = new byte[encryptedDataWithIv.length - iv.length];
        System.arraycopy(encryptedDataWithIv, 0, iv, 0, iv.length);
        System.arraycopy(encryptedDataWithIv, iv.length, encryptedPassword, 0, encryptedPassword.length);
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getUrlDecoder().decode(masterPassword), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM_DATA);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedPassword = cipher.doFinal(encryptedPassword);
        return new String(decryptedPassword, StandardCharsets.UTF_8);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 1 << COST, SIZE);
        SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

}
