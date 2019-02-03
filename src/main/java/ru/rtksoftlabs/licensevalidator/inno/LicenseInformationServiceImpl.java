package ru.rtksoftlabs.licensevalidator.inno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensevalidator.License;
import ru.rtksoftlabs.licensevalidator.LicenseInformationService;
import ru.rtksoftlabs.licensevalidator.ProtectedObjectsService;

import java.time.LocalDate;

@Service
@Profile("inno")
public class LicenseInformationServiceImpl implements LicenseInformationService {
    @Autowired
    ProtectedObjectsService protectedObjectsService;

    @Override
    public License getLicenseInformation() {
        License license = new License();

        license.setBeginDate(LocalDate.now().minusDays(10));
        license.setEndDate(LocalDate.now().plusDays(20));

        license.setProtectedObjects(protectedObjectsService.getProtectedObjects());

        return license;
    }
}
