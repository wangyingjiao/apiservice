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

        AESCrypt crypt = new AESCrypt("1234567812345678");
        String encrypt = crypt.encrypt("111111");
        System.out.println(encrypt);

//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("loginName","13716663777");
//        jsonObject.addProperty("password","21232f297a57a5a743894a0e4a801fc3");
//        jsonObject.addProperty("requestTimestamp","1494913897272");
//
//        String json = "{\"loginName\":\"13716663777\",\"password\":\"21232f297a57a5a743894a0e4a801fc3\",\"requestTimestamp\":\"1494915513101\"}";
//       // String encode = Base64Encoder.encode(jsonObject.toString().getBytes("UTF-8"));
//        String encode = Base64Encoder.encode(json.getBytes("UTF-8"));
//
//        System.out.println(encode);
//
//        //String jsonRequest ="{\"loginName\":\"13716663777\",\"password\":\"21232f297a57a5a743894a0e4a801fc3\",\"requestTimestamp\":\"1494908307760\"}";
////        AESCrypt aesCrypt = new AESCrypt(AppTypeKeyEnum.ANDROID_CUSTOMER.getSeed());
////        String encrypt = aesCrypt.encrypt(encode);
////        System.out.println(encrypt);
//
//
//
//        String aaa = "bzV1ZjhSRjZ5bGo4K1lYNkxMSnhEQWJKalB1WlF2d0xZTnRaZ0tOYm52cSt3TEpmbWYvNWw5bVFxRmNpeVhITTFFVDd6eWc5UGZhSzEwa28vYXFTaHVENy9qTXV2UUR1R0I5c0J0K05WVER1bEZXZ2Z1VzVncWI5TEFYbFpPTU05OVNXTVYzL1BiTTFYT3ozZkR2MjRXaDVUSTZVd3MvWThTVVBKT2hCRk5zPQ==";
//
////        System.out.println(AppTypeKeyEnum.ANDROID_CUSTOMER.getMd5Key());
////        String stringMD5 = MD5Util.getStringMD5(aaa + AppTypeKeyEnum.ANDROID_CUSTOMER.getMd5Key());
////        System.out.println("md555555555555:"+stringMD5);
//
////        String decrypt = aesCrypt.decrypt(encrypt);
////        System.out.println(":::::"+decrypt);
//
//
//        String bbb = "NlMvSldiTlh3b2JNRFFZYzZPakx3VFVUa2ppajBOc3lVVU80RThpdW8zTXgycDhTbWJROVN4SzE1bENXOFRjSzVtWXMwa2lteno4bmhEenJROUFPeGQ5bXNZdFZ6S0xyVVZoMUthK2pTZjhDVzcwcFp0RnVRZmlRZXJMbm1TRmc=";
//
////        String decryptBBB = aesCrypt.decrypt(bbb);
////        System.out.println(":::::2"+decryptBBB);
//

    }

}
