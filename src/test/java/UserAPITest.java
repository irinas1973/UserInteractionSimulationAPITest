import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigLoader;
import utils.User;
import utils.UserApi;
import utils.WireMockServerSetup;

import java.io.IOException;
import java.util.List;

public class UserAPITest {
    private List<User> userList;
    private WireMockServerSetup wireMockServerSetup;

    Logger logger;

    @BeforeClass
    public void setup() throws IOException {
        logger = LogManager.getLogger(this.getClass());
        wireMockServerSetup = new WireMockServerSetup();
        userList = ConfigLoader.loadConfig("src/test/resources/users.json");
        loadingStubData("****************************************");
        logger.info("*****Start transfer users from JSON*****");
        for (User user : userList) {
            UserApi.TestAPIResponse response = UserApi.createUser(user);
            User addedUser = new Gson().fromJson(response.getResponseStr(), new TypeToken<>() {
            });
            logger.info("Created: {}", addedUser);
        }
        logger.info("*****Users are received from users.json file*****");
    }

    @Test(priority = 1)
    public void testCreateUser() throws IOException {
        logger.info("*****Test POST user started*****");
        User user = new User(3, "Yan", "yan.kovlev@gmail.com");
        UserApi.TestAPIResponse
                response = UserApi.createUser(user);
        userList.add(user);
        Assert.assertEquals(response.getCode(), 201);
        User addedUser = new Gson().fromJson(response.getResponseStr(), new TypeToken<>() {
        });
        logger.info("Created: {}", addedUser);
        logger.info("*****Test POST completed*****");
        loadingStubData("New user added to WireMock mappings");
    }

    private void loadingStubData(String message) {
        wireMockServerSetup.updateWireMockStub(userList);
        logger.info(message);
    }

    @Test(priority = 2)
    public void testGetUser() throws IOException {
        int userId = 3;
        logger.info("*****Test GET user with UserID=%d started*****", userId);
        UserApi.TestAPIResponse response = UserApi.getUser(userId);
        try{
        Assert.assertEquals(response.getCode(), 200);
        User user = new Gson().fromJson(response.getResponseStr(), new TypeToken<>() {
        });
        logger.info("Received: {}", user);
        logger.info("*****Test GET completed*****");
        } catch (AssertionError e) {
            logger.error("UserID = {} not found. AssertionError occurred: {}", userId, e.getMessage());
            throw e;
        }

    }

    @Test(priority = 3)
    public void testUpdateUser() throws IOException {
        int userId = 1;
        String userName = "Ira";
        String userEmail = "irina.shekhter@gmail.com";
        logger.info("*****Test PUT user with UserID=%d started*****", userId);
        UserApi.TestAPIResponse response = UserApi.updateUser(new User(userId, userName, userEmail));
        updateUserList(userId, userName, userEmail);
        try{
        Assert.assertEquals(response.getCode(), 200);
        User user = new Gson().fromJson(response.getResponseStr(), new TypeToken<>() {
        });
        logger.info("Updated: {}", user);
        logger.info("*****Test PUT completed*****");
        loadingStubData("User updated in WireMock mappings");
        } catch (AssertionError e) {
            logger.error("UserID = {} not found. AssertionError occurred: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4)
    public void testDeleteUser() throws IOException {
        int userId = 2;
        logger.info("*****Test DELETE user with UserID={} started*****", userId);
        UserApi.TestAPIResponse response = UserApi.deleteUser(userId);
        try {
            Assert.assertEquals(response.getCode(), 200);
            User user = getUserById(userId);
            userList.remove(user);
            logger.info("Deleted: {}", user);
            logger.info("*****Test DELETE completed*****");
            loadingStubData("User deleted from WireMock mappings");
        } catch (AssertionError e) {
            logger.error("UserID = {} not found. AssertionError occurred: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5)
    public void testGetNonExistentEndpoint() throws IOException {
        int userId = 3;
        logger.info("*****Test GET to not existent endpoint started*****", userId);
        UserApi.TestAPIResponse response = UserApi.getNonExistentEndpoint(userId);
        Assert.assertEquals(response.getCode(), 404);
        logger.error("Endpoint not exist");
        logger.info("*****Test GET NonExistentEndpoint completed*****");
    }

    private User getUserById(int userId) {
        for (User user : userList) {
            if (user.getId() == userId)
                return user;
        }
        return null;
    }

    @AfterClass
    public void tearDown() {
        wireMockServerSetup.tearDown();
        ConfigLoader.writeUsersToJsonFile(userList, "src/test/resources/");
    }

    private void updateUserList(int userId, String userName, String userEmail) {
        for (User user : userList) {
            if (user.getId() == userId) {
                user.setName(userName);
                user.setEmail(userEmail);
            }
        }
    }

}
