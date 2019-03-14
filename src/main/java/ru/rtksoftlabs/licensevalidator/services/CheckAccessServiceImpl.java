package ru.rtksoftlabs.licensevalidator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensevalidator.dao.LicenseInformationData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private boolean find(String protectedObject) {
        Pattern patt = Pattern.compile("^(.*?)/");
        Matcher matcher = patt.matcher(protectedObject);

        String appName = "";

        if (matcher.find()) {
            appName = matcher.group(1);
        }

        List<String> pathesToLeafs = licenseInformationData.getProtectedObjects().get(appName);

        if (pathesToLeafs != null) {
            List<String> listOfStringsWithPathToAllLeafs = new ArrayList<>();
            listOfStringsWithPathToAllLeafs.add(protectedObject);

            return pathesToLeafs.containsAll(listOfStringsWithPathToAllLeafs);
        }

        return false;
    }

    private boolean checkDates() {
        ConcurrentMap<String, LocalDate> licenseDates = licenseInformationData.getLicenseDates();

        LocalDate beginDate = licenseDates.get("beginDate");
        LocalDate endDate = licenseDates.get("endDate");

        return checkLicenseDates(beginDate, endDate);
    }

    @Override
    public boolean checkAccess(String protectedObject) {
        if (checkDates()) {
            return find(protectedObject);
        }

        return false;
    }
}
