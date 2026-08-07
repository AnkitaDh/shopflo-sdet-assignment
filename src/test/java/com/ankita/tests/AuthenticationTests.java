package com.ankita.tests;

import com.ankita.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class AuthenticationTests extends BaseApiTest {

    @Test(description = "TC05 - Login with valid credentials")
    public void validLogin() {

        String requestBody = """
                {
                    "username": "mor_2314",
                    "password": "83r5^_"
                }
                """;

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .when()
                        .post("/auth/login")
                        .then()
                        .extract()
                        .response();


        Assert.assertEquals(
                response.statusCode(),
                201,
                "Login should be successful"
        );

        Assert.assertNotNull(
                response.jsonPath().getString("token"),
                "Token should not be null"
        );

        Assert.assertFalse(
                response.jsonPath().getString("token").isEmpty(),
                "Token should not be empty"
        );
    }

    @Test(description = "TC06 - Login with invalid credentials")
    public void invalidLogin() {

        String requestBody = """
                {
                    "username": "wrong_user",
                    "password": "wrong_password"
                }
                """;

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .when()
                        .post("/auth/login")
                        .then()
                        .extract()
                        .response();

        Assert.assertTrue(
                response.statusCode() == 401 || response.statusCode() == 400,
                "Expected authentication failure"
        );
    }

    @Test(description = "TC07 - Verify JWT token is returned")
    public void verifyTokenReturned() {

        String requestBody = """
                {
                    "username": "mor_2314",
                    "password": "83r5^_"
                }
                """;

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .when()
                        .post("/auth/login")
                        .then()
                        .extract()
                        .response();

        String token = response.jsonPath().getString("token");

        Assert.assertNotNull(token);
        Assert.assertTrue(token.length() > 20);
    }

}