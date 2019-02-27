package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class CheckAccessController {
    @Autowired
    private LicenseInformationData licenseInformationData;

    @PostMapping(value = "/check-access")
    public Response checkAccess(@RequestBody ProtectedObject protectedObject) throws NoSuchAlgorithmException {
        Response response = new Response();
        response.setTimestamp(Instant.now());

        LocalDate now = LocalDate.now();

        if (licenseInformationData.getLicense().getBeginDate().isBefore(now) && licenseInformationData.getLicense().getEndDate().isAfter(now)) {
            if (licenseInformationData.getLicense().find(protectedObject)) {
                response.setResponse(true);
            }
        }

        response.setHash(response.generateHash(protectedObject));

        return response;
    }
}
