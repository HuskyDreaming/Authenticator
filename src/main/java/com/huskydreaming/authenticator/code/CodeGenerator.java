package com.huskydreaming.authenticator.code;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CodeGenerator {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String generate(String secret, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        String hmac = "HmacSHA1";
        Base32 base32 = new Base32();
        byte[] decodedKey = base32.decode(secret);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, hmac);
        Mac mac = Mac.getInstance(hmac);
        mac.init(signKey);

        return convertHash(mac.doFinal(timeData(time)));
    }

    public static String[] generateBackupCodes(int length, int amount) {
        String[] backupCodes = new String[amount];
        for (int i = 0; i < amount; i++) {
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();
            while (salt.length() < length) {
                salt.append(SALT_CHARS.charAt((int) (rnd.nextFloat() * SALT_CHARS.length())));
            }
            backupCodes[i] = salt.toString();
        }
        return backupCodes;
    }

    private static byte[] timeData(long time) {
        byte[] data = new byte[8];
        long value = time;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        return data;
    }

    private static String convertHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;

        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= Math.pow(10, 6);

        return String.format("%0" + 6 + "d", truncatedHash);
    }
}
