package ru.rtksoftlabs.licensevalidator;

import java.time.LocalDate;

public interface CheckAccessService {
    boolean checkLicenseDates(LocalDate beginDate, LocalDate endDate);
    boolean checkAccess(ProtectedObject protectedObject);
}
