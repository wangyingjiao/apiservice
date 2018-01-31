package com.thinkgem.jeesite.common.utils;

import com.alibaba.fastjson.JSONObject;

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
    //解密
    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64Decoder.decodeToBytes(cryptedText);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return Base64Decoder.decode(decryptedText);
    }

    public static void main(String[] args) throws Exception{

        AESCrypt crypt = new AESCrypt("seed");
        String s3="{\"pageSize\":15,\"pageNo\":1}";
        String s1="{\"username\":\"13508070808\",\"password\":\"1234qwer\"}";
//        String s3="{\"id\":\"9\"}";
//        String s2="{\"username\":\"15711445637\",\"password\":\"1234qwer\",\"serviceStatus\":wait_service,\"majorSort\":all}";
        String baseEncode = Base64Encoder.encode(s1);//base64加密
        String aesEncode = crypt.encrypt(baseEncode);//aes加密
        System.out.println("2次加密"+aesEncode);
        //MD5获取MD5操作之后值
        String stringMD5 = MD5Util.getStringMD5(aesEncode);
        System.out.println("md5=="+stringMD5);

    }

    public static void main1(String[] args) throws Exception {

        AESCrypt crypt = new AESCrypt("seed");
        String encrypt2 = crypt.encrypt("{\"pageSize\":15,\"pageNo\":1,\"serviceStatus\":\"wait_service\",\"majorSort\":\"all\"}");

        System.out.println(encrypt2+"+++++++++加密后");

        String m1 = MD5Util.getStringMD5(encrypt2);

        System.out.println("md5盐***************8"+m1);
        String encrypt = crypt.encrypt("111111");
        String ceshi="8JcpNJqcCvEQm+n8qF9Gvyo7g6G2yIlpMb7JzZo/B3NR7CVfn2zgvnj9mQ5r/2Fwjq2xA3q5/wvxQOfnSzALGi7IbU3zp6EYHGaRbfkwHgI=";
       // String ceshi="mZm5PLLx3w169jtA6dFrQ1G9LnQAHYc54uO6aIW9v/uUL+lhF1/mluLggO/RE+49KGLszOaxJigSZ4bWwVJ1P9+T6i1E5YagL9lAa2vnA/wbaEKXwmL8cuOdZDI3i9tDkQKchldZde8ubHXoQK7gRw==";
        String decrypt1 = crypt.decrypt(ceshi);
        System.out.println("解密+++"+decrypt1);


        String ss="mZm5PLLx3w169jtA6dFrQ1G9LnQAHYc54uO6aIW9v/uUL+lhF1/mluLggO/RE+49KGLszOaxJigSZ4bWwVJ1P9+T6i1E5YagL9lAa2vnA/wbaEKXwmL8cuOdZDI3i9tDkQKchldZde8ubHXoQK7gRw==";
        String decrypt = crypt.decrypt(ss);
        System.out.println("解密"+decrypt);
        //
        String md11 = MD5Util.getRequestBodyMD5(decrypt);
        String md5 = MD5Util.getRequestBodyMD5(ss);

    }

}
