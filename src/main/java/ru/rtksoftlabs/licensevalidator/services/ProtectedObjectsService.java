package ru.rtksoftlabs.licensevalidator.services;

import ru.rtksoftlabs.licensevalidator.shared.ProtectedObject;

import java.util.List;

public interface ProtectedObjectsService {
    List<ProtectedObject> getProtectedObjects();
}
