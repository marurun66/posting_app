package com.marurun66.postingapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//- email: 유효한 이메일 형식 (@포함)
public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

