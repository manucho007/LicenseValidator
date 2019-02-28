package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class CheckAccessController {
    @Autowired
    private CheckAccessService checkAccessService;

    @PostMapping(value = "/check-access")
    public Response checkAccess(@RequestBody ProtectedObject protectedObject) throws NoSuchAlgorithmException {
        Response response = new Response();

        response.setTimestamp(Instant.now());

        response.setResponse(false);

        if (checkAccessService.checkAccess(protectedObject)) {
            response.setResponse(true);
        }

        response.setHash(response.generateHash(protectedObject));

        return response;
    }
}
