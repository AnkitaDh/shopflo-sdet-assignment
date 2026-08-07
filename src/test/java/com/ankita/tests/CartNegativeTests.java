package com.ankita.tests;

import com.ankita.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CartNegativeTests extends BaseApiTest {

    @Test(description = "TC09 - Get cart with invalid ID")
    public void getCartWithInvalidId() {

        Response response =
                given()
                        .pathParam("id", 999999)
                        .when()
                        .get("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        System.out.println("Status Code : " + response.statusCode());
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 404,
                "Expected 404 for invalid cart ID.");
    }

    @Test(description = "TC10 - Update non-existing cart")
    public void updateInvalidCart() {

        String requestBody = """
                {
                    "userId": 1,
                    "products": [
                        {
                            "productId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .pathParam("id", 999999)
                        .body(requestBody)
                        .when()
                        .put("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        System.out.println("Status Code : " + response.statusCode());
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 404,
                "Expected 404 while updating an invalid cart.");
    }

    @Test(description = "TC11 - Delete non-existing cart")
    public void deleteInvalidCart() {

        Response response =
                given()
                        .pathParam("id", 999999)
                        .when()
                        .delete("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        System.out.println("Status Code : " + response.statusCode());
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 404,
                "Expected 404 while deleting an invalid cart.");
    }
}