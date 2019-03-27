package ru.rtksoftlabs.licensevalidator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.licensevalidator.exceptions.LicenseInstallationException;
import ru.rtksoftlabs.licensevalidator.exceptions.LicenseValidationException;
import ru.rtksoftlabs.licensevalidator.services.LicenseInformationService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LicenseValidatorController {
    @Autowired
    private LicenseInformationService licenseInformationService;

    @GetMapping("/license-information")
    public License getLicenseInformation() {
        return licenseInformationService.getLicenseInformation();
    }

    @PostMapping("/install-license")
    public License installLicense(MultipartHttpServletRequest request) throws IOException {
        for (Map.Entry<String, MultipartFile> elem : request.getFileMap().entrySet()) {
            if (elem.getValue().getOriginalFilename().endsWith(".zip")) {
                if (licenseInformationService.installLicense(elem.getValue().getBytes())) {
                    return licenseInformationService.getLicenseInformation();
                }
                else {
                    throw new LicenseValidationException("License validation failed");
                }
            }
        }

        throw new LicenseInstallationException("License installation failed");
    }

    private byte[] getZipFile(Map<String, MultipartFile> multipartFiles) {
        for (Map.Entry<String, MultipartFile> elem : multipartFiles.entrySet()) {
            if (elem.getValue().getOriginalFilename().endsWith(".zip")) {
                try {
                    return elem.getValue().getBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new LicenseInstallationException("License installation failed");
    }
}
