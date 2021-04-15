package com.example.hospital.Utils;

import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SecurityUtils {
    public static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdnS/LflJjuj4M9V63SPe3" +
            "YUx0uFD/oVoPRwG2p1HXNSp6x+h7ImVAns9Bx5aKKthTH6V70W0TAdreCwS7WyzakvnHu4zWhGX77JM7/dEgdU" +
            "5LSK8sI7YLz4bKhCpjBQGXJiKj/3InDPidWzw6w53Ce207HUzrYgR71rM3/OfewwIDAQAB";
    public static String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ2dL8t+UmO6Pgz1XrdI97dhTHS4" +
            "UP+hWg9HAbanUdc1KnrH6HsiZUCez0HHlooq2FMfpXvRbRMB2t4LBLtbLNqS+ce7jNaEZfvskzv9" +
            "0SB1TktIrywjtgvPhsqEKmMFAZcmIqP/cicM+J1bPDrDncJ7bTsdTOtiBHvWszf8597DAgMBAAEC" +
            "gYBb6uuQtV6/IkJFtGNEaJ1uqKO5/jPeoO6wsixhpTqpUywu5p7CENET2onsRsWYVlxKPc54Yy5F" +
            "Q3OswqhDy2xgL5AFcCboDHCipVojA/6lQsWnvQL1Go/NkAf88YmIMvcNV6TrlBW3TWOr9D6+/LUW" +
            "Hjbmr744bE7eRODAV/PVYQJBAOZe0uIm5mXm+eoyE8BT7SVu7XWeQZNtDNvD0a6h4QxQA+fkftVP" +
            "9icAfRxZEi3WH8aCtUJJkDdzo3u11XdPQF8CQQCvJjBoQI+5l4dlBfsN3jvLr9H6U1Hx20EF47t/" +
            "FbfSEHHJ0LQ6frVyEqQr2MSuL+RhE4ko+PWpB62meFpmKuwdAkEAxI73xDqIrz3K0wZzT9DMMPpa" +
            "5dZoAVA0fnawPB6nFIhZLM0LYxpc3p5OIZfmKPHgHtJ7sdlukcG7JdzaDHi0ZQJAe6pqKWHUWQUd" +
            "av3zChKsg5+rkaS8yhi163OlEhECjkZgIU/DwT1v3ZA97FuMWzSjestxX8WQpn0uZci6g0KxHQJA" +
            "BA1IIAnT7LlLHDWDXAH3B4U60GQZh7BbfF7m3nQvttLeUOc2hpklN3Rr3hpKRDghX5m5iZeiIPri" +
            "nPq25FdzAA==";
    /**
     * 解密
     * @param cipherText 密文
     * @return 返回解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String cipherText) throws Exception{
        PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
        byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(cipherText), privateKey);
        String decryptStr = new String(decryptByte,"utf-8");
        return decryptStr;
    }
    /**
     * 加密
     * @param plainTest 明文
     * @return	返回加密后的密文
     * @throws Exception
     */
    public static String encrypt(String plainTest) throws Exception{
        PublicKey publicKey = RSAUtils.loadPublicKey(PUBLIC_KEY);
        byte[] encryptByte = RSAUtils.encryptData(plainTest.getBytes(), publicKey);
        String afterencrypt = Base64Utils.encode(encryptByte);
        return afterencrypt;
    }
}