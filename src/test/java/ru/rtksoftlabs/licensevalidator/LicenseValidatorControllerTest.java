package ru.rtksoftlabs.licensevalidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inno")
public class LicenseValidatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @SpyBean
    private LicenseInformationService licenseInformationService;

    @SpyBean
    private SignatureService signatureService;

    @Autowired
    private ZipLicenseService zipLicenseService;

    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    License license;

    @Before
    public void before() {
        license = new License();

        license.setBeginDate(LocalDate.now().minusDays(10));
        license.setEndDate(LocalDate.now().plusDays(20));

        license.setProtectedObjects(protectedObjectsService.getProtectedObjects());
    }

    @Test
    public void getLicenseInformationShouldReturnObjects() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String content = mapper.writeValueAsString(license);

        mockMvc.perform(get("/api/license-information")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @Test
    public void installLicenseShouldReturnTrue() throws Exception {
        SignedLicenseContainer signedLicenseContainerTest = new SignedLicenseContainer();

        Mockito.when(licenseInformationService.getNewSignedLicenseContainer()).thenReturn(signedLicenseContainerTest);

        byte[] licenseBytes = license.toJson().getBytes();

        KeyPair keyPair = signatureService.generateKeyPair();

        KeyStore keyStore = signatureService.getKeyStoreWithCertificate(keyPair);

        Certificate certificate = keyStore.getCertificate(signatureService.getKeyAliasName());

        Mockito.doReturn(certificate).when(signatureService).loadCertificate();

        byte[] signBytes = signatureService.sign(licenseBytes, keyPair.getPrivate());

        SignedLicenseContainer signedLicenseContainer = new SignedLicenseContainer();

        signedLicenseContainer.setLicense(licenseBytes);

        signedLicenseContainer.setSign(signBytes);

        zipLicenseService.zipLicense(signedLicenseContainer);

        signedLicenseContainer.setZipFileName(signedLicenseContainerTest.getZipFileName());

        Mockito.verify(fileService, Mockito.atLeastOnce()).load(any(String.class));

        Mockito.verify(fileService, Mockito.never()).save(any(byte[].class), any(String.class));

        MockMultipartFile firstFile = new MockMultipartFile("license.zip", signedLicenseContainer.getZipFileName(), MediaType.APPLICATION_OCTET_STREAM_VALUE, signedLicenseContainer.getZip());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/install-license")
                .file(firstFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(license.toJson()))
                .andExpect(status().isOk());
    }
}
