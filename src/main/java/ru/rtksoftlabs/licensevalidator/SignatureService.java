package ru.rtksoftlabs.licensevalidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public interface SignatureService {
    KeyPair generateKeyPair() throws NoSuchAlgorithmException;
    byte[] sign(byte[] messageBytes, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    boolean verify(byte[] messageBytes, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    boolean verify(byte[] messageBytes, byte[] signatureBytes, Certificate certificate) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    Certificate loadCertificate() throws FileNotFoundException, CertificateException;
    Keys loadOrCreateKeyStore() throws GeneralSecurityException, IOException;
    KeyStore getKeyStoreWithCertificate(KeyPair keyPair) throws GeneralSecurityException, IOException;
    String getKeyAliasName();
}
