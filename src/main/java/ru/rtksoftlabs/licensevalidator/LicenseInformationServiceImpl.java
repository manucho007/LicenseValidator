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
    ProtectedObjectsService protectedObjectsService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    ZipLicenseService zipLicenseService;

    @Autowired
    FileService fileService;

    @Autowired
    LicenseInformationData licenseInformationData;

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

                licenseInformationData.setLicense(mapper.readValue(new String(licenseBytes), License.class));
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

            if (signatureService.verify(signedLicenseContainer.getLicense(), signedLicenseContainer.getSign(), certificate)) {
                String jsonString = new String(signedLicenseContainer.getLicense());

                ObjectMapper mapper = new ObjectMapper();

                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                licenseInformationData.setLicense(mapper.readValue(jsonString, License.class));

                fileService.save(signedLicenseContainer.getLicense(), licenseInnerFileName);
                fileService.save(signedLicenseContainer.getSign(), signatureInnerFileName);

                return true;
            }

            return false;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean installLicense(byte[] zipBytes) throws IOException {
        SignedLicenseContainer signedLicenseContainer = getNewSignedLicenseContainer();

        signedLicenseContainer.setZip(zipBytes);

        zipLicenseService.unzipLicense(signedLicenseContainer);

        return validateLicense(signedLicenseContainer);
    }
}
