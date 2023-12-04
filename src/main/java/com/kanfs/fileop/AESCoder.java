package com.kanfs.fileop;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESCoder {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static SecretKey generateKeyFromPassword(String password) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        // 使用PBKDF2生成16字节的密钥材料
        char[] passwordChars = password.toCharArray();
        byte[] salt = new byte[16]; // 可以自定义盐值
        int iterationCount = 1000; // 可以自定义迭代次数
        int keyLength = 128; // 128位密钥长度

        KeySpec keySpec = new PBEKeySpec(passwordChars, salt, iterationCount, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(keySpec).getEncoded();

        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static void encryptFile(BufferedInputStream bis, BufferedOutputStream bos, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidKeySpecException {
        SecretKey secretKey = generateKeyFromPassword(password);
        // 创建AES加密器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 加密数据
        CipherInputStream cipherInputStream = new CipherInputStream(bis, cipher);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = cipherInputStream.read(buffer)) != -1)
            bos.write(buffer, 0, bytesRead);

        cipherInputStream.close();
    }


    public static void decryptFile(BufferedInputStream bis, BufferedOutputStream bos, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidKeySpecException {
        SecretKey secretKey = generateKeyFromPassword(password);
        // 创建AES解密器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 解密数据
        CipherOutputStream cipherOutputStream = new CipherOutputStream(bos, cipher);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            cipherOutputStream.write(buffer, 0, bytesRead);
        }

        cipherOutputStream.close();
    }

    public static byte[] encryptCompressFile(byte[] data, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = generateKeyFromPassword(password);
        // 创建AES加密器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 返回加密后的数据
        return cipher.doFinal(data);

    }

    public static byte[] decryptCompressFile(byte[] data, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = generateKeyFromPassword(password);
        // 创建AES加密器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 返回解密后的数据
        return cipher.doFinal(data);
    }
}
