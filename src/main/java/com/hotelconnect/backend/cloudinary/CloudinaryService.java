package com.hotelconnect.backend.cloudinary;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class CloudinaryService {

    public Map<String, Object> generateSignatureData() throws NoSuchAlgorithmException {
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, String> paramsToSign = new TreeMap<>();
        paramsToSign.put("timestamp", String.valueOf(timestamp));
        String uploadPreset = "paucasesnoves";
        paramsToSign.put("upload_preset", uploadPreset);

        String signature = generateSignature(paramsToSign);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", timestamp);
        response.put("signature", signature);
        String apiKey = "648868535917264";
        response.put("apiKey", apiKey);
        String cloudName = "dglxd4bqz";
        response.put("cloudName", cloudName);

        return response;
    }

    private String generateSignature(Map<String, String> params) throws NoSuchAlgorithmException {
        StringBuilder toSign = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            toSign.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        toSign.setLength(toSign.length() - 1); // Remove last &

        String apiSecret = "BCW1rCcvYuP7p6nik4ps0sMOjFQ";
        toSign.append(apiSecret);

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = md.digest(toSign.toString().getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}
