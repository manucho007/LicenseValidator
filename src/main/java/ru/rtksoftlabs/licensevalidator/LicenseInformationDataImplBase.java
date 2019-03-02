package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
    private void loadLicense() {
        try {
            SignedLicenseContainer signedLicenseContainer = new SignedLicenseContainer();

            byte[] licenseBytes = fileService.load(licenseInnerFileName);

            byte[] signatureBytes = fileService.load(signatureInnerFileName);

            signedLicenseContainer.setLicense(licenseBytes);
            signedLicenseContainer.setSign(signatureBytes);

            if (licenseInformationService.validateLicense(signedLicenseContainer)) {
                License license = licenseInformationService.mapToObject(licenseBytes);

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

            for (ProtectedObject protectedObject: license.getProtectedObjects()) {
                protectedObjects.put(protectedObject.data, protectedObject.returnListOfStringsWithPathToAllLeafs());
            }

            this.license.setBeginDate(license.getBeginDate());
            this.license.setEndDate(license.getEndDate());

            this.license.setProtectedObjects(license.getProtectedObjects());
        }
    }
}
