package com.sib.ibanklosucl.utilies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESUtil {
    @Value("${sso.token.aeskey}")
    private String SECRET_KEY ;



    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public  String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        return new String(cipher.doFinal(decoded));
    }


    public  String generateUniqueHash(String ino) {
        try {
            // Create a SHA-256 digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(ino.getBytes());

            // Encode hash bytes to Base32 (which uses only alphanumeric characters)
            Base32 base32 = new Base32();
            String hashBase32 = base32.encodeAsString(hashBytes);

            // Combine part of the Base32 hash with the ino to ensure uniqueness
            String uniqueHash = hashBase32.substring(0, 16) + ino.substring(0, 4);

            // Ensure the final string is exactly 20 characters long
            return uniqueHash.length() > 20 ? uniqueHash.substring(0, 20) : uniqueHash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }


}
