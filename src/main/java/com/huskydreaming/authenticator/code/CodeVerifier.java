package com.huskydreaming.authenticator.code;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CodeVerifier {
    private final long time;

    public CodeVerifier(long time) {
        this.time = time;
    }

    public boolean isValid(String secret, String code) {
        long current = Math.floorDiv(time, 30);
        boolean success = false;

        for (int i = -1; i <= 1; i++) {
            success = checkCode(secret, current + i, code) || success;
        }
        return success;
    }

    private boolean checkCode(String secret, long counter, String code) {
        String actualCode;
        try {
            actualCode = CodeGenerator.generate(secret, counter);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return timeSafeStringComparison(actualCode, code);
    }

    private boolean timeSafeStringComparison(String actual, String old) {
        byte[] aBytes = actual.getBytes();
        byte[] bBytes = old.getBytes();

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }

}
