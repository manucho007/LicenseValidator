package ru.rtksoftlabs.licensevalidator;

import java.io.IOException;

public interface LicenseInformationService {
    License getLicenseInformation();
    SignedLicenseContainer getNewSignedLicenseContainer();
    boolean installLicense(byte[] zipBytes) throws IOException;
}
