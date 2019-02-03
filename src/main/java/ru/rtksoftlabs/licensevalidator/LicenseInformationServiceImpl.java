package ru.rtksoftlabs.licensevalidator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
public class LicenseInformationServiceImpl implements LicenseInformationService {
    @Override
    public License getLicenseInformation() {
        return null;
    }
}
