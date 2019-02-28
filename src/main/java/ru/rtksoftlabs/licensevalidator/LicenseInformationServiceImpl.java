package ru.rtksoftlabs.licensevalidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    @Value("${license.inner.file.name}")
    String licenseInnerFileName;

    @Value("${signature.inner.file.name}")
    String signatureInnerFileName;

    @PostConstruct
    public void loadLicense() {
        try {
            SignedLicenseContainer signedLicenseContainer = new SignedLicenseContainer();

            byte[] licenseBytes = fileService.load(licenseInnerFileName);

            byte[] signatureBytes = fileService.load(signatureInnerFileName);

            signedLicenseContainer.setLicense(licenseBytes);
            signedLicenseContainer.setSign(signatureBytes);

            if (validateLicense(signedLicenseContainer)) {
                ObjectMapper mapper = new ObjectMapper();

                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                License license = mapper.readValue(new String(licenseBytes), License.class);

                if (checkAccessService.checkLicenseDates(license.getBeginDate(), license.getEndDate())) {
                    licenseInformationData.setLicense(license);
                }
            }
        } catch (IOException | RuntimeException e) {

        }
    }

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    @Override
    public License getLicenseInformation() {
        return licenseInformationData.getLicense();
    }

    private boolean validateLicense(SignedLicenseContainer signedLicenseContainer) {
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

            ObjectMapper mapper = new ObjectMapper();

            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            License license = mapper.readValue(jsonString, License.class);

            if (checkAccessService.checkLicenseDates(license.getBeginDate(), license.getEndDate())) {
                licenseInformationData.setLicense(license);

                fileService.save(signedLicenseContainer.getLicense(), licenseInnerFileName);
                fileService.save(signedLicenseContainer.getSign(), signatureInnerFileName);

                return true;
            }
        }

        return false;
    }
}
