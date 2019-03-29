package ru.rtksoftlabs.licensevalidator.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!inno")
public class LicenseInformationDataImpl extends LicenseInformationDataImplBase {

}
