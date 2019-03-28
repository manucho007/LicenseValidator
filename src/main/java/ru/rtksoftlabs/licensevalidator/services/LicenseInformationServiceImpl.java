package ru.rtksoftlabs.licensevalidator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.LicenseCommons.services.FileService;
import ru.rtksoftlabs.LicenseCommons.services.JsonMapperService;
import ru.rtksoftlabs.LicenseCommons.services.SignatureService;
import ru.rtksoftlabs.LicenseCommons.services.ZipLicenseService;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensevalidator.dao.LicenseInformationData;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

@Service
public class LicenseInformationServiceImpl implements LicenseInformationService {
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ZipLicenseService zipLicenseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private LicenseInformationData licenseInformationData;

    @Autowired
    private CheckAccessService checkAccessService;

    @Autowired
    private JsonMapperService jsonMapperService;

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    @Override
    public License getLicenseInformation() {
        return licenseInformationData.getLicense();
    }

    public boolean validateLicense(SignedLicenseContainer signedLicenseContainer) {
        try {
            Certificate certificate = signatureService.loadCertificate();

            return signatureService.verify(signedLicenseContainer.getLicense(), signedLicenseContainer.getSign(), certificate);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean installLicense(byte[] zipBytes) throws IOException {
        SignedLicenseContainer signedLicenseContainer = getNewSignedLicenseContainer();

        signedLicenseContainer.setZip(zipBytes);

        zipLicenseService.unzipLicense(signedLicenseContainer);

        if (validateLicense(signedLicenseContainer)) {
            String jsonString = new String(signedLicenseContainer.getLicense());

            License license = jsonMapperService.generateLicense(jsonString);

            if (checkAccessService.checkLicenseDates(license.getBeginDate(), license.getEndDate())) {
                licenseInformationData.setLicense(license);

                fileService.save(signedLicenseContainer.getLicense(), licenseInformationData.getLicenseInnerFileName());
                fileService.save(signedLicenseContainer.getSign(), licenseInformationData.getSignatureInnerFileName());

                return true;
            }
        }

        return false;
    }
}
