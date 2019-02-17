package ru.rtksoftlabs.licensevalidator.inno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.rtksoftlabs.licensevalidator.License;
import ru.rtksoftlabs.licensevalidator.LicenseInformationData;
import ru.rtksoftlabs.licensevalidator.ProtectedObjectsService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
@Profile("inno")
public class LicenseInformationDataImpl implements LicenseInformationData {
    private License license;

    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @PostConstruct
    public void init() {
        license = new License();

        license.setBeginDate(LocalDate.now().minusDays(10));
        license.setEndDate(LocalDate.now().plusDays(20));

        license.setProtectedObjects(protectedObjectsService.getProtectedObjects());
    }

    @Override
    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }
}
