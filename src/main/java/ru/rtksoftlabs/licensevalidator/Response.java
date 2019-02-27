package ru.rtksoftlabs.licensevalidator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class Response {
    private boolean response;
    private Instant timestamp;
    private byte[] hash;

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] generateHash(ProtectedObject protectedObject) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        String saltString = "RtKDec78";

        String saltArrayString = "RecS9";

        List<String> stringPathList = protectedObject.returnListOfStringsWithPathToAllLeafs();

        String stringToEncrypt = timestamp.toString();

        char[] stringPathArray = stringPathList.get(0).toCharArray();

        if (stringPathList.size() > 0) {
            for (int i = 0; i < stringPathArray.length; i+=2) {
                stringToEncrypt += saltArrayString + stringPathArray[i];
            }
        }

        stringToEncrypt += saltString + response;

        messageDigest.update(stringToEncrypt.getBytes());

        byte[] returnBytes = Base64.getEncoder().encode(messageDigest.digest());

        String returnString = new String(returnBytes);

        return Base64.getEncoder().encode(messageDigest.digest());
    }
}
