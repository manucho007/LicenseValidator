package ru.rtksoftlabs.licensevalidator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import ru.rtksoftlabs.LicenseCommons.shared.ResponseSignUtil;
import ru.rtksoftlabs.licensevalidator.services.CheckAccessService;

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
    public ResponseSignUtil checkAccess(final HttpServletRequest request) throws NoSuchAlgorithmException {
        String protectedObject = getProtectedObjectFromPath(request);

        boolean access = false;

        if (checkAccessService.checkAccess(protectedObject)) {
            access = true;
        }

        return new ResponseSignUtil(protectedObject, access, Instant.now());
    }
}
