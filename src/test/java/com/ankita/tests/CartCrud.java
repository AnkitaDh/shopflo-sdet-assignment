package com.ankita.tests;

import com.ankita.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CartCrud extends BaseApiTest {

    @Test(description = "TC01 - Create a new cart")
    public void createCart() {

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
                        .body(requestBody)
                        .when()
                        .post("/carts")
                        .then()
                        .extract()
                        .response();

        Assert.assertEquals(response.statusCode(), 201,
                "Cart should be created successfully.");

        Assert.assertEquals(response.jsonPath().getInt("userId"), 1);

        Assert.assertEquals(response.jsonPath().getInt("products[0].productId"), 1);

        Assert.assertEquals(response.jsonPath().getInt("products[0].quantity"), 2);
    }

    @Test(description = "TC02 - Get a cart by ID")
    public void getSingleCart() {

        Response response =
                given()
                        .pathParam("id", 1)
                        .when()
                        .get("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        Assert.assertEquals(response.statusCode(), 200,
                "Cart should be fetched successfully.");

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);

        Assert.assertTrue(response.jsonPath().getInt("userId") > 0);

        Assert.assertFalse(response.jsonPath().getList("products").isEmpty());
    }

    @Test(description = "TC03 - Update an existing cart")
    public void updateCart() {

        String requestBody = """
                {
                    "userId": 1,
                    "products": [
                        {
                            "productId": 2,
                            "quantity": 3
                        }
                    ]
                }
                """;

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .pathParam("id", 1)
                        .body(requestBody)
                        .when()
                        .put("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        Assert.assertEquals(response.statusCode(), 200,
                "Cart should be updated successfully.");

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);

        Assert.assertEquals(response.jsonPath().getInt("userId"), 1);

        Assert.assertEquals(response.jsonPath().getInt("products[0].productId"), 2);

        Assert.assertEquals(response.jsonPath().getInt("products[0].quantity"), 3);
    }

    @Test(description = "TC04 - Delete a cart")
    public void deleteCart() {

        Response response =
                given()
                        .pathParam("id", 1)
                        .when()
                        .delete("/carts/{id}")
                        .then()
                        .extract()
                        .response();

        Assert.assertEquals(response.statusCode(), 200,
                "Cart should be deleted successfully.");

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
    }

}