package ru.rtksoftlabs.licensevalidator.inno;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensevalidator.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

@Service
@Profile("inno")
public class LicenseInformationServiceImpl implements LicenseInformationService {
    private License license;

    @Autowired
    ProtectedObjectsService protectedObjectsService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    ZipLicenseService zipLicenseService;

    @Autowired
    FileService fileService;

    public LicenseInformationServiceImpl() {
        license = new License();
    }

    @PostConstruct
    public void loadLicense() {
        try {
            SignedLicenseContainer signedLicenseContainer = new SignedLicenseContainer();

            byte[] licenseBytes = fileService.load("license.lic");

            byte[] signatureBytes = fileService.load("license.sign");

            signedLicenseContainer.setLicense(licenseBytes);
            signedLicenseContainer.setSign(signatureBytes);

            if (validateLicense(signedLicenseContainer)) {
                ObjectMapper mapper = new ObjectMapper();

                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                license = mapper.readValue(new String(licenseBytes), License.class);
            }
        } catch (IOException | RuntimeException e) {

        }
    }

    @Override
    public License getLicenseInformation() {
        return license;
    }

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    private boolean validateLicense(SignedLicenseContainer signedLicenseContainer) {
        try {
            Certificate certificate = signatureService.loadCertificate();

            if (signatureService.verify(signedLicenseContainer.getLicense(), signedLicenseContainer.getSign(), certificate)) {
                String jsonString = new String(signedLicenseContainer.getLicense());

                ObjectMapper mapper = new ObjectMapper();

                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                license = mapper.readValue(jsonString, License.class);

                fileService.save(signedLicenseContainer.getLicense(), "license.lic");
                fileService.save(signedLicenseContainer.getSign(), "license.sign");

                return true;
            }

            return false;
        } catch (IOException | GeneralSecurityException e) {
            //e.printStackTrace();
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
