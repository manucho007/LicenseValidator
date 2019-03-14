package ru.rtksoftlabs.licensevalidator.services;

import java.time.LocalDate;

public interface CheckAccessService {
    boolean checkLicenseDates(LocalDate beginDate, LocalDate endDate);
    boolean checkAccess(String protectedObject);
}
