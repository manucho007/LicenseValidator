package ru.rtksoftlabs.licensevalidator.services;

import ru.rtksoftlabs.licensevalidator.util.SignedLicenseContainer;

import java.io.IOException;

public interface ZipLicenseService {
    void zipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
    void unzipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
}
