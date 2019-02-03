package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LicenseValidatorController {
    @Autowired
    LicenseInformationService licenseInformationService;

    @GetMapping("/api/license-information")
    public License getLicenseInformation() {
        //{"beginDate":"2018-12-27", "endDate":"2018-12-29", "protectedObjects":[{"name":"App1","components":["Component1","Component2","Component3"]},{"name":"App2","components":["Component1","Component2","Component3"]}]}

        return licenseInformationService.getLicenseInformation();
    }
}
