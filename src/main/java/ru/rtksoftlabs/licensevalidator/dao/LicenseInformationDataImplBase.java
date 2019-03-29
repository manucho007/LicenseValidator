package ru.rtksoftlabs.licensevalidator.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.rtksoftlabs.LicenseCommons.services.FileService;
import ru.rtksoftlabs.LicenseCommons.services.JsonMapperService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensevalidator.services.LicenseInformationService;
import ru.rtksoftlabs.licensevalidator.services.CheckAccessService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LicenseInformationDataImplBase implements LicenseInformationData {
    private final License license;

    private final ConcurrentMap<String, List<String>> protectedObjects;
    private final ConcurrentMap<String, LocalDate> licenseDates;

    @Value("${license.inner.file.name}")
    private String licenseInnerFileName;

    @Value("${signature.inner.file.name}")
    private String signatureInnerFileName;

    @Autowired
    private FileService fileService;

    @Autowired
    private LicenseInformationService licenseInformationService;

    @Autowired
    private CheckAccessService checkAccessService;

    @Autowired
    private JsonMapperService jsonMapperService;

    @Override
    public String getLicenseInnerFileName() {
        return licenseInnerFileName;
    }

    @Override
    public String getSignatureInnerFileName() {
        return signatureInnerFileName;
    }

    public LicenseInformationDataImplBase() {
        license = new License();

        protectedObjects = new ConcurrentHashMap<>();
        licenseDates = new ConcurrentHashMap<>();
    }

    @PostConstruct
    @Override
    public void loadLicense() {
        try {
            SignedLicenseContainer signedLicenseContainer = new SignedLicenseContainer();

            byte[] licenseBytes = fileService.load(licenseInnerFileName);

            byte[] signatureBytes = fileService.load(signatureInnerFileName);

            signedLicenseContainer.setLicense(licenseBytes);
            signedLicenseContainer.setSign(signatureBytes);

            if (licenseInformationService.validateLicense(signedLicenseContainer)) {
                License license = jsonMapperService.generateLicense(new String(licenseBytes));

                if (checkAccessService.checkLicenseDates(license.getBeginDate(), license.getEndDate())) {
                    setLicense(license);
                }
            }
        } catch (IOException | RuntimeException e) {

        }
    }

    @Override
    public ConcurrentMap<String, List<String>> getProtectedObjects() {
        return protectedObjects;
    }

    @Override
    public ConcurrentMap<String, LocalDate> getLicenseDates() {
        return licenseDates;
    }

    @Override
    public License getLicense() {
        synchronized (license) {
            return license;
        }
    }

    @Override
    public void setLicense(License license) {
        synchronized (this.license) {
            licenseDates.put("beginDate", license.getBeginDate());
            licenseDates.put("endDate", license.getEndDate());

            protectedObjects.clear();

            for (ProtectedObject protectedObject: license.getProtectedObjects()) {
                protectedObjects.put(protectedObject.getData(), protectedObject.returnListOfStringsWithPathToAllLeafs());
            }

            this.license.setBeginDate(license.getBeginDate());
            this.license.setEndDate(license.getEndDate());

            this.license.setProtectedObjects(license.getProtectedObjects());
        }
    }
}
