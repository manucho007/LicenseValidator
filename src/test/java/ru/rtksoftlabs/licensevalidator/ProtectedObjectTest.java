package ru.rtksoftlabs.licensevalidator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("inno")
public class ProtectedObjectTest {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    private String generateJson(ProtectedObject protectedObject) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.writeValueAsString(protectedObject);
    }

    private ProtectedObject generateObject(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.readValue(jsonString, ProtectedObject.class);
    }

    @Test
    public void toJsonTest() throws JsonProcessingException {
        ProtectedObject protectedObject = protectedObjectsService.getProtectedObjects().get(0);

        String content = generateJson(protectedObject);

        String expectedString = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"},{\"data\":\"sc3\"}]},{\"data\":\"Roles\"}]}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void toObjectTest() throws IOException {
        String jsonString = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"},{\"data\":\"sc3\"}]},{\"data\":\"Roles\"}]}";

        ProtectedObject protectedObject = generateObject(jsonString);

        String jsonStringExpected = generateJson(protectedObject);

        assertThat(jsonString).isEqualTo(jsonStringExpected);
    }

    @Test
    public void returnTrueWhenFindTest() throws IOException {
        String stringForSearch = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc3\"}]}]}";

        ProtectedObject protectedObject = generateObject(stringForSearch);

        ProtectedObject protectedObjectManyNodes = protectedObjectsService.getProtectedObjects().get(0);

        boolean isFind = protectedObjectManyNodes.find(protectedObject);

        assertThat(isFind).isTrue();

        stringForSearch = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc2\"}]}]}";

        protectedObject = generateObject(stringForSearch);

        isFind = protectedObjectManyNodes.find(protectedObject);

        assertThat(isFind).isTrue();
    }

    @Test
    public void returnFalseWhenNotFindTest() throws IOException {
        String stringForSearch = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc4\"}]}]}";

        ProtectedObject protectedObject = generateObject(stringForSearch);

        ProtectedObject protectedObjectManyNodes = protectedObjectsService.getProtectedObjects().get(0);

        boolean isFind = protectedObjectManyNodes.find(protectedObject);

        assertThat(isFind).isFalse();

        stringForSearch = "{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\"}]}";

        protectedObject = generateObject(stringForSearch);

        isFind = protectedObjectManyNodes.find(protectedObject);

        assertThat(isFind).isFalse();
    }
}
