package com.huskydreaming.authenticator.code;

public class CodeVerifier {

    private final CodeGenerator codeGenerator;
    private final long time;

    public CodeVerifier(CodeGenerator codeGenerator, long time) {
        this.codeGenerator = codeGenerator;
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
        String actualCode = codeGenerator.generate(secret, counter);
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
