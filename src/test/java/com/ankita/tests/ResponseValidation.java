package com.ankita.tests;

import com.ankita.base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ResponseValidation extends BaseApiTest {

    @Test(description = "TC07 - Validate Cart Response Schema")
    public void validateCartSchema() {

        given()
                .pathParam("id", 1)
                .when()
                .get("/carts/{id}")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/cart-schema.json"));
    }
}