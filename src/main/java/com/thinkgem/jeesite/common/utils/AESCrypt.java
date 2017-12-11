package com.thinkgem.jeesite.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author vipin.cb , vipin.cb@experionglobal.com <br>
 * Sep 27, 2013, 5:18:34 PM <br>
 * Package:- <b>com.veebow.util</b> <br>
 * Project:- <b>Veebow</b>
 * <p>
 */
public class AESCrypt {
    private final Cipher cipher;
    private final SecretKeySpec key;
    private AlgorithmParameterSpec spec;
    public static final String SEED_16_CHARACTER = "U1MjU1M0FDOUZ.Qz";

    public AESCrypt() throws Exception {
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    public AlgorithmParameterSpec getIV() {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64Encoder.encode(encrypted).getBytes(), "UTF-8");
        return encryptedText;
    }

    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64Decoder.decode(cryptedText).getBytes("UTF-8");
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return decryptedText;
    }

    public static void main(String[] args) throws Exception {
        AESCrypt crypt = new AESCrypt();
        System.out.println(crypt.decrypt("111111"));
    }
}