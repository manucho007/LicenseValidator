package ru.rtksoftlabs.licensevalidator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.rtksoftlabs.LicenseCommons.exceptions.ApiExceptionMessage;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({LicenseValidationException.class, LicenseInstallationException.class})
    public ResponseEntity<ApiExceptionMessage> handleLicenseException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ApiExceptionMessage(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
