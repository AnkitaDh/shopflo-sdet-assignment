package com.ankita.base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {

    protected static final String BASE_URL = "https://fakestoreapi.com";

    @BeforeClass
    public void setupApi() {
        RestAssured.baseURI = BASE_URL;
    }
}