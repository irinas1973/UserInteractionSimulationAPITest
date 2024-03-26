package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConfigLoader {
    public static List<User> loadConfig(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return new Gson().fromJson(reader, new TypeToken<>() {
            });
        }
    }

    public static void writeUsersToJsonFile(List<User> userList, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(userList);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String dateTimeString = now.format(formatter);

            String filename = "users_" + dateTimeString + ".json";

            File file = new File(filePath + filename);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
