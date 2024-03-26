package utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class WireMockServerSetup {
    public static final int PORT = 8080;

    private WireMockServer wireMockServer;

    public WireMockServerSetup() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    public static void configureEndpoints(User user) {
        // GET endpoint
        stubFor(get(urlEqualTo("/user/" + user.getId()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(userToJson(user))));


        // POST endpoint
        stubFor(post(urlEqualTo("/user"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(userToJson(user))));

        // PUT endpoint
        stubFor(put(urlEqualTo("/user/" + user.getId()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(userToJson(user))));

        // DELETE endpoint
        stubFor(delete(urlEqualTo("/user/" + user.getId()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(String.format("%s deleted successfully", user))));
    }


    public void tearDown() {
        wireMockServer.stop();
    }

    private static String userToJson(User user) {
        return new Gson().toJson(user);
    }

    public void updateWireMockStub(List<User> userList) {
        wireMockServer.resetMappings();
        for (User user : userList) {
            configureEndpoints(user);
        }
    }
}
