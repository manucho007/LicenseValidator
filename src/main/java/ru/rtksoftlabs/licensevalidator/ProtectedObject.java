package ru.rtksoftlabs.licensevalidator;

import java.util.List;
import java.util.Map;

public class ProtectedObject {
    private String name;
    private Map<String, List<ProtectedObject>> components;

    public ProtectedObject() {
    }

    public ProtectedObject(String name, Map<String, List<ProtectedObject>> components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<ProtectedObject>> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        return "ProtectedObject{" +
                "name='" + name + '\'' +
                ", components=" + components +
                '}';
    }
}
