package ru.rtksoftlabs.licensevalidator;

import java.io.IOException;

public interface LicenseInformationService {
    License getLicenseInformation();
    SignedLicenseContainer getNewSignedLicenseContainer();
    boolean installLicense(byte[] zipBytes) throws IOException;
    boolean validateLicense(SignedLicenseContainer signedLicenseContainer);
    License mapToObject(byte[] licenseBytes) throws IOException;
    License mapToObject(String licenseString) throws IOException;
}
