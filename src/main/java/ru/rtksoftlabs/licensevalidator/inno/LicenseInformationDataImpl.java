package ru.rtksoftlabs.licensevalidator.inno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.rtksoftlabs.LicenseCommons.services.ProtectedObjectsService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.licensevalidator.dao.LicenseInformationDataImplBase;

import java.time.LocalDate;

@Component
@Profile("inno")
public class LicenseInformationDataImpl extends LicenseInformationDataImplBase {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Override
    public void loadLicense() {
        ProtectedObjects protectedObjects = protectedObjectsService.getProtectedObjects();

        License license = new License();

        license.setBeginDate(LocalDate.now().minusDays(10));
        license.setEndDate(LocalDate.now().plusDays(20));

        license.setProtectedObjects(protectedObjects);

        setLicense(license);
    }
}
