package com.alcea.utils;

public class CheckPassword {
    private final static String strongPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,}$";
    private final static String validPassword = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$";

    public static boolean checkPasswordValid(String password){
        return password.matches(validPassword);
    }
    public static boolean checkPasswordStrong(String password){
        return password.matches(strongPassword);
    }
}
