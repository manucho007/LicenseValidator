package ru.rtksoftlabs.licensevalidator.dao;

import ru.rtksoftlabs.LicenseCommons.util.License;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface LicenseInformationData {
    License getLicense();
    void setLicense(License license);
    ConcurrentMap<String, List<String>> getProtectedObjects();
    ConcurrentMap<String, LocalDate> getLicenseDates();
    String getLicenseInnerFileName();
    String getSignatureInnerFileName();
}
