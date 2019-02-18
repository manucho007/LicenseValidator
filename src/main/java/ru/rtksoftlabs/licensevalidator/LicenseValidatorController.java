package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LicenseValidatorController {
    @Autowired
    LicenseInformationService licenseInformationService;

    @GetMapping("/license-information")
    public License getLicenseInformation() {
        return licenseInformationService.getLicenseInformation();
    }

    @PostMapping("/install-license")
    @ResponseBody
    public License installLicense(MultipartHttpServletRequest request) throws IOException {
        for (Map.Entry<String, MultipartFile> elem : request.getFileMap().entrySet()) {
            if (elem.getValue().getOriginalFilename().endsWith(".zip")) {
                if (licenseInformationService.installLicense(elem.getValue().getBytes())) {
                    return licenseInformationService.getLicenseInformation();
                }
                else {
                    throw new RuntimeException("Лицензия НЕ провалидирована!");
                }
            }
        }

        throw new RuntimeException("Лицензия НЕ установлена! ");
    }
}
