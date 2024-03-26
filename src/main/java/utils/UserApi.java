package utils;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UserApi {
    private static final String MOCK_SERVER_URL = "http://localhost:8080";

    public static class TestAPIResponse {
        private final int code;
        private final String responseStr;

        private TestAPIResponse(int code, HttpEntity entity) throws IOException {
            this.code = code;
            this.responseStr = entity == null ? null : EntityUtils.toString(entity);
        }

        public int getCode() {
            return code;
        }

        public String getResponseStr() {
            return responseStr;
        }
    }

    public static TestAPIResponse createUser(User user) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(MOCK_SERVER_URL + "/user");
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(user.getUserPayload().toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
                return new TestAPIResponse(resp.getStatusLine().getStatusCode(), resp.getEntity());
            }
        }
    }

    public static TestAPIResponse getUser(int userId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(MOCK_SERVER_URL + "/user/" + userId);
            httpGet.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse resp = httpClient.execute(httpGet)) {
                return new TestAPIResponse(resp.getStatusLine().getStatusCode(), resp.getEntity());
            }
        }
    }

    public static TestAPIResponse updateUser(User user) throws IOException {
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(user);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(MOCK_SERVER_URL + "/user/" + user.getId());
            httpPut.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(jsonPayload);
            httpPut.setEntity(entity);
            try (CloseableHttpResponse resp = httpClient.execute(httpPut)) {
                return new TestAPIResponse(resp.getStatusLine().getStatusCode(), resp.getEntity());
            }
        }
    }

    public static TestAPIResponse deleteUser(int userId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(MOCK_SERVER_URL + "/user/" + userId);
            httpDelete.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse resp = httpClient.execute(httpDelete)) {
                return new TestAPIResponse(resp.getStatusLine().getStatusCode(), resp.getEntity());
            }
        }
    }

    public static TestAPIResponse getNonExistentEndpoint(int userId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(MOCK_SERVER_URL + "/nonexistent/" + userId);
            httpGet.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse resp = httpClient.execute(httpGet)) {
                return new TestAPIResponse(resp.getStatusLine().getStatusCode(), resp.getEntity());
            }
        }
    }

}
