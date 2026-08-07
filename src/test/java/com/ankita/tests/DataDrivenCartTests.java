package com.ankita.tests;

import com.ankita.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class DataDrivenCartTests extends BaseApiTest {

    @DataProvider(name = "productIds")
    public Object[][] productIds() {
        return new Object[][]{
                {1},
                {2},
                {3}
        };
    }

    @Test(dataProvider = "productIds",
            description = "TC08 - Validate cart creation with multiple product IDs")
    public void createCartWithDifferentProducts(int productId) {

        String requestBody = """
                {
                    "userId": 1,
                    "products": [
                        {
                            "productId": %d,
                            "quantity": 2
                        }
                    ]
                }
                """.formatted(productId);

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .when()
                        .post("/carts")
                        .then()
                        .extract()
                        .response();

        Assert.assertEquals(response.statusCode(), 201);

        Assert.assertEquals(
                response.jsonPath().getInt("products[0].productId"),
                productId
        );

        Assert.assertEquals(
                response.jsonPath().getInt("products[0].quantity"),
                2
        );
    }
}