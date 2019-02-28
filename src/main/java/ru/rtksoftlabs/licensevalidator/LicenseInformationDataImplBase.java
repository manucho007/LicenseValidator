package ru.rtksoftlabs.licensevalidator;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LicenseInformationDataImplBase implements LicenseInformationData {
    private final License license;

    private final ConcurrentMap<String, List<String>> protectedObjects;
    private final ConcurrentMap<String, LocalDate> licenseDates;

    public LicenseInformationDataImplBase() {
        license = new License();

        protectedObjects = new ConcurrentHashMap<>();
        licenseDates = new ConcurrentHashMap<>();
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
