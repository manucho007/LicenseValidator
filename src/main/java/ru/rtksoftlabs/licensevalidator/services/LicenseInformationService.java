package ru.rtksoftlabs.licensevalidator.services;

import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;

import java.io.IOException;

public interface LicenseInformationService {
    License getLicenseInformation();
    SignedLicenseContainer getNewSignedLicenseContainer();
    boolean installLicense(byte[] zipBytes) throws IOException;
    boolean validateLicense(SignedLicenseContainer signedLicenseContainer);
}
