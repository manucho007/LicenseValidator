package ru.rtksoftlabs.licensevalidator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public License mapToObject(byte[] licenseBytes) throws IOException {
        return mapToObject(new String(licenseBytes));
    }

    public String toJson(License license) throws JsonProcessingException {
        return getJsonMapper().writeValueAsString(license);
    }

    @Override
    public ObjectMapper getJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper;
    }

    public License mapToObject(String licenseString) throws IOException {
        return getJsonMapper().readValue(licenseString, License.class);
    }

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

            License license = mapToObject(jsonString);

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
