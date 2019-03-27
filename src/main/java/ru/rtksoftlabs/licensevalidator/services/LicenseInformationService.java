package ru.rtksoftlabs.licensevalidator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;

import java.io.IOException;

public interface LicenseInformationService {
    License getLicenseInformation();
    SignedLicenseContainer getNewSignedLicenseContainer();
    boolean installLicense(byte[] zipBytes) throws IOException;
    boolean validateLicense(SignedLicenseContainer signedLicenseContainer);
    License mapToObject(byte[] licenseBytes) throws IOException;
    License mapToObject(String licenseString) throws IOException;
    ObjectMapper getJsonMapper();
    String toJson(License license) throws JsonProcessingException;
}