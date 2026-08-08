package com.ankita.tests;

import com.ankita.base.BaseTest;
import com.ankita.pages.LoginPage;
import com.ankita.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @DataProvider(name = "users")
    public Object[][] users() {
        return new Object[][]{
                {"standard_user", "secret_sauce"},
                {"locked_out_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"},
                {"error_user", "secret_sauce"},
                {"visual_user", "secret_sauce"}
        };
    }

    @Test(dataProvider = "users", description = "Validate login for all users")
    public void validateLogin(String username, String password) {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        ProductsPage productsPage = new ProductsPage(driver);

        if (username.equals("locked_out_user")) {
            Assert.assertTrue(!productsPage.isProductsPageDisplayed(),
                    "locked_out_user should NOT be able to log in");

        } else {

        }

        if (productsPage.isProductsPageDisplayed()) {

            System.out.println("PASS : " + username + " logged in successfully.");

        } else {

            String errorMessage = loginPage.getErrorMessage();

            Assert.fail(
                    "Login failed for user: " + username +
                            "\nApplication Error: " + errorMessage
            );
        }
    }
}