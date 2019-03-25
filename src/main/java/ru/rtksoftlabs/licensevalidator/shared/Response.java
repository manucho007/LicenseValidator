package ru.rtksoftlabs.licensevalidator.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

public class Response {
    private boolean access;
    private Instant timestamp;
    private byte[] hash;

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
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

    public byte[] generateHash(String protectedObject) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");

        String saltString = "RtKDec78";

        String stringToEncrypt = protectedObject.replace("/", "");

        stringToEncrypt += access;

        stringToEncrypt += timestamp.toString();

        stringToEncrypt += saltString;

        messageDigest.update(stringToEncrypt.getBytes());

        return Base64.getEncoder().encode(messageDigest.digest());
    }
}
