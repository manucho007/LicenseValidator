package ru.rtksoftlabs.licensevalidator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileService {
    void save(byte[] content, String fileName) throws IOException;
    byte[] load(String fileName) throws IOException;
    FileInputStream loadInputStream(String fileName) throws FileNotFoundException;
}
