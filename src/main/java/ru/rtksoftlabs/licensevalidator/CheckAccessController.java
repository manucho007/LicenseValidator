package ru.rtksoftlabs.licensevalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class CheckAccessController {
    @Autowired
    private CheckAccessService checkAccessService;

    private String getProtectedObjectFromPath(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();

        return apm.extractPathWithinPattern(bestMatchPattern, path);
    }

    @GetMapping(value = "/check-access/**")
    public Response checkAccess(final HttpServletRequest request) throws NoSuchAlgorithmException {
        String protectedObject = getProtectedObjectFromPath(request);

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
