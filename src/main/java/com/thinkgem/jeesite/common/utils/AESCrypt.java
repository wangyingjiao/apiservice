package com.thinkgem.jeesite.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @Author jh
 * @Data 2017/4/20.
 * @Email 942329288@qq.com
 */
public class AESCrypt {

    private final Cipher cipher;

    private final SecretKeySpec key;

    private AlgorithmParameterSpec spec;

    public static final String SEED_16_CHARACTER = "seed";

    public AESCrypt(String appTypeKey) throws Exception {
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(appTypeKey.getBytes("UTF-8"));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    public AlgorithmParameterSpec getIV() {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception {
        //plainText = new String(Base64.encodeBase64(plainText.toString().getBytes("UTF-8")), "UTF-8");
 // plainText = Base64Encoder.encode(plainText.toString().getBytes("UTF-8"));
//        System.out.println("base64:"+plainText);
//        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
//        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
//        return new String(Base64Encoder.encode(encrypted).getBytes("UTF-8"), "UTF-8");

//        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
//        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
//        String encryptedText = new String(Base64Encoder.encode(encrypted));
//        return encryptedText;

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
//        String encryptedText = new String(Base64.encode(encrypted,
//                Base64.DEFAULT), "UTF-8");
        String encryptedText = Base64Encoder.encode(encrypted);
        return encryptedText;


    }

    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64Decoder.decodeToBytes(cryptedText);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return Base64Decoder.decode(decryptedText);
    }

    public static void main(String[] args) throws Exception {

        AESCrypt crypt = new AESCrypt("seed");
        String encrypt = crypt.encrypt("111111");
        System.out.println(encrypt);

        String s ="+8GdDMXX3UU392sllC7+1qYvGtHwoQBR9obfLRF2yj4Q67LbfObCMrJ9m4fBFrZD/5+BO4gI9kMZ4i7fi7UK2kV3oZ+1IT+gQmZ6AzKoCbmNGpwNos1l0augz2IgSeFbOU2LhuDFZcNqjUBOhRDFxGwx5JKlQXNlpp3MjSIwa+DYdP5vxXakYffzpVOVdjTei7xZ+U4PleHDQ6qpEhyqMpIjw2t/jh7m50xRxp9QCcq42T1GFEGuKjXM12UnDSAeKWZu4ZkQE8Q94UHFLEg5ccqJnszQjsdBptoEv+srsP/o5KaVF6d725jC82nsKArvqoLYFOWIwDSVxE7zQSYUlF+Pin88tM99rGpwylpRnqtvejQ7N0aZnhCraSx4hq8k9QJkuFucwT00CxtOgZuh7A==";
        String decrypt = crypt.decrypt(s);
        System.out.println(decrypt);

    }

}
