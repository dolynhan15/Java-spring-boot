package com.qooco.boost.data.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public final class CipherKeys {
    private static final String AES_KEY = "AES";
    private static final String RSA_KEY = "RSA";
    private static final String LIST_KEYS = "RSA/ECB/PKCS1Padding";
    private static final String EMPTY_STRING = "";
    private static final String CHARSET_NAME = "UTF-8";
    private static final int AES_KEY_SIZE = 128;
    private static final int AES_KEY_LENGTH = 16;

    public static SecretKey generateAESKey() {
        String secretString = RandomStringUtils.randomAlphanumeric(AES_KEY_LENGTH);
        return new SecretKeySpec(secretString.getBytes(), 0, secretString.getBytes().length, AES_KEY);
    }

    public static String encryptByAES(String plainText, String secretKeyString) {
        try {
            if (StringUtils.isNotBlank(secretKeyString) && StringUtils.isNotBlank(plainText)) {
                SecretKey secretKey = CipherKeys.getSecretKey(secretKeyString);
                byte[] encryptedContent = CipherKeys.encryptByAES(plainText, secretKey);
                return CipherKeys.convertToBase64String(encryptedContent);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return plainText;
    }

    public static byte[] encryptByAES(String plainText, SecretKey secKey) {
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance(AES_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert aesCipher != null;
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return aesCipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptByAES(String encryptedText, String secretKeyString) {
        try {
            if (StringUtils.isNotBlank(encryptedText) && StringUtils.isNotBlank(secretKeyString)) {
                byte[] encryptedContent = CipherKeys.convertToByte(encryptedText);

                SecretKey secretKey = CipherKeys.getSecretKey(secretKeyString);
                byte[] decryptedContent = CipherKeys.decryptByAES(encryptedContent, secretKey);
                return new String(decryptedContent);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    public static byte[] decryptByAES(byte[] encryptedText, SecretKey secKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert cipher != null;
            cipher.init(Cipher.DECRYPT_MODE, secKey);
            try {
                return cipher.doFinal(encryptedText);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    private static String decryptByAES(byte[] byteCipherText, byte[] decryptedKey) {
        SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, AES_KEY);
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance(AES_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert aesCipher != null;
            aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] bytePlainText;
        try {
            bytePlainText = aesCipher.doFinal(byteCipherText);
            return convertToBase64String(bytePlainText);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    public static String encryptByRSA(String msg, PublicKey key) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert cipher != null;
            cipher.init(Cipher.PUBLIC_KEY, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return encodeBase64String(cipher.doFinal(msg.getBytes(CHARSET_NAME)));
    }

    public static byte[] decryptByRSA(PrivateKey prKey, byte[] encryptedKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert cipher != null;
            cipher.init(Cipher.PRIVATE_KEY, prKey);
            try {
                return cipher.doFinal(encryptedKey);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedKey;
    }

    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_KEY);
        return kf.generatePublic(spec);
    }

    public static SecretKey getSecretKey(String encodedSecKey) {
        return new SecretKeySpec(encodedSecKey.getBytes(), 0, encodedSecKey.getBytes().length, "AES");
    }

    public static String convertToBase64String(SecretKey secKey) {
        return new String(secKey.getEncoded());
    }

    public static String convertToBase64String(byte[] encodedKey) {
        return encodeBase64String(encodedKey);
    }

    public static byte[] convertToByte(String encodedKey) {
        return Base64.getDecoder().decode(encodedKey);
    }


    public static void main(String[] args) {
        String secretString = "xZY2QcSAXXy9ergV";
        SecretKey originalKey = new SecretKeySpec(secretString.getBytes(), 0, secretString.getBytes().length, "AES");
        String plainText = "1234";
        byte[] encryptedBytes = encryptByAES(plainText, originalKey);
        String encryptedText = convertToBase64String(encryptedBytes);


        SecretKey secretKey = new SecretKeySpec(secretString.getBytes(), 0, secretString.getBytes().length, "AES");
        byte[] encryptedContentByte = convertToByte(encryptedText);
        byte[] contentByte = decryptByAES(encryptedContentByte, secretKey);
        String content =   new String(contentByte);
        String secretString1 =   new String(secretKey.getEncoded());
        System.out.println("content = "+content);
        System.out.println("secretString1 = "+secretString1);
        System.out.println("generatedString = "+secretString);
        System.out.println("encryptedText = "+encryptedText);
    }
}
