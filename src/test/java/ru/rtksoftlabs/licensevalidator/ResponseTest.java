package ru.rtksoftlabs.licensevalidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("inno")
public class ResponseTest {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Test
    public void generateHashTest() throws NoSuchAlgorithmException {
        Response response = new Response();

        Instant instant = Instant.now();

        response.setTimestamp(instant);

        response.setResponse(true);

        ProtectedObject protectedObject = protectedObjectsService.getProtectedObjects().get(0);

        byte[] generatedHash = response.generateHash(protectedObject.returnListOfStringsWithPathToAllLeafs().get(0));

        Response expectedResponse = new Response();

        expectedResponse.setTimestamp(instant);

        expectedResponse.setResponse(true);

        byte[] expectedHash = expectedResponse.generateHash(protectedObject.returnListOfStringsWithPathToAllLeafs().get(0));

        assertThat(generatedHash).isEqualTo(expectedHash);
    }
}
