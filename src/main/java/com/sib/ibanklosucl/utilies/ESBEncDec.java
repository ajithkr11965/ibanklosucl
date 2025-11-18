package com.sib.ibanklosucl.utilies;


import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Component
public class ESBEncDec {

    @Value("${esb.client_id}")
    private  String  client_id;
    @Value("${esb.client_secretkey}")
    private  String  client_secretkey;




    public  String decrypt(String encrypted) throws Exception {
        try {
            client_secretkey = client_secretkey.substring(0, 16);
            client_id = client_id.substring(0, 32);

            IvParameterSpec ivParameterSpec;
            SecretKeySpec secretKeySpec;
            Cipher cipher = null;
            ivParameterSpec = new IvParameterSpec(client_secretkey.getBytes("UTF-8"));
            secretKeySpec = new SecretKeySpec(client_id.getBytes("UTF-8"), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(Hex.decodeHex(encrypted.toCharArray()));
            return new String(decryptedBytes);
        }
        catch (Exception e){
            throw e;
        }
    }

    public  String encrypt(String toBeEncrypt) throws Exception {
        try {
            client_secretkey = client_secretkey.substring(0, 16);
            client_id = client_id.substring(0, 32);

            IvParameterSpec ivParameterSpec;
            SecretKeySpec secretKeySpec;
            Cipher cipher = null;
            ivParameterSpec = new IvParameterSpec(client_secretkey.getBytes("UTF-8"));
            secretKeySpec = new SecretKeySpec(client_id.getBytes("UTF-8"), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(toBeEncrypt.getBytes());
            return new String(Hex.encodeHex(encrypted));
        } catch (Exception e) {
            throw e;
        }

    }
}