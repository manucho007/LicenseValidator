package ru.rtksoftlabs.licensevalidator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class LicenseInformationDataImpl implements LicenseInformationData {
    private License license;

    public LicenseInformationDataImpl() {
        license = new License();
    }

    @Override
    public License getLicense() {
        return license;
    }

    @Override
    public void setLicense(License license) {
        this.license = license;
    }
}
