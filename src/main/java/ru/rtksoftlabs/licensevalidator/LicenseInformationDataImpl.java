package ru.rtksoftlabs.licensevalidator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class LicenseInformationDataImpl extends LicenseInformationDataImplBase {

}
