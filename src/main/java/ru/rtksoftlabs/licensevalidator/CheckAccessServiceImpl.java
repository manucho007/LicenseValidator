package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Service
public class CheckAccessServiceImpl implements CheckAccessService {
    @Autowired
    private LicenseInformationData licenseInformationData;

    @Override
    public boolean checkLicenseDates(LocalDate beginDate, LocalDate endDate) {
        if (beginDate != null && endDate != null) {
            LocalDate now = LocalDate.now();

            if (beginDate.isBefore(now) && endDate.isAfter(now)) {
                return true;
            }
        }

        return false;
    }

    private boolean find(ProtectedObject protectedObject) {
        String appName = protectedObject.data;

        List<String> pathesToLeafs = licenseInformationData.getProtectedObjects().get(appName);

        if (pathesToLeafs != null) {
            return pathesToLeafs.containsAll(protectedObject.returnListOfStringsWithPathToAllLeafs());
        }

        return false;
    }

    @Override
    public boolean checkAccess(ProtectedObject protectedObject) {
        ConcurrentMap<String, LocalDate> licenseDates = licenseInformationData.getLicenseDates();

        LocalDate beginDate = licenseDates.get("beginDate");
        LocalDate endDate = licenseDates.get("endDate");

        if (checkLicenseDates(beginDate, endDate)) {
            return find(protectedObject);
        }

        return false;
    }
}
