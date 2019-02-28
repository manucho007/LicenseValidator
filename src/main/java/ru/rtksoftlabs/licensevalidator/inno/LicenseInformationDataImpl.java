package ru.rtksoftlabs.licensevalidator.inno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.rtksoftlabs.licensevalidator.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile("inno")
public class LicenseInformationDataImpl extends LicenseInformationDataImplBase {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @PostConstruct
    public void init() {
        List<ProtectedObject> protectedObjects = protectedObjectsService.getProtectedObjects();

        License license = new License();

        license.setBeginDate(LocalDate.now().minusDays(10));
        license.setEndDate(LocalDate.now().plusDays(20));

        license.setProtectedObjects(protectedObjects);

        setLicense(license);
    }
}
