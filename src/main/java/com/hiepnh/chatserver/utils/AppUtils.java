package com.hiepnh.chatserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppUtils {

    public static String convertByteArrayToString(byte[] input){
        return new String(input, StandardCharsets.UTF_8);
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertJsonToObject(String json, Class<T> className) {
        T rs;
        try {
            rs = mapper.readValue(json, className);
        } catch (Exception e) {
            rs = null;
        }
        return rs;
    }


    public static String hashString(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            byte[] var4 = array;
            int var5 = array.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                byte b = var4[var6];
                sb.append(Integer.toHexString(b & 255 | 256), 1, 3);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException var8) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(hashString("taominh"));
    }
}
