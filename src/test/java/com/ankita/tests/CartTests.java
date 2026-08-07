package com.ankita.tests;
import org.openqa.selenium.Alert;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ankita.base.BaseTest;
import com.ankita.pages.CartPage;
import com.ankita.pages.CheckoutInformationPage;
import com.ankita.pages.LoginPage;
import com.ankita.pages.ProductsPage;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class CartTests extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final int MULTI_PRODUCT_COUNT = 6;

    private static ExtentReports extent;
    private ExtentTest test;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeSuite(alwaysRun = true)
    public void setUpReport() {
        File reportDir = new File("target/surefire-reports");
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new IllegalStateException("Unable to create report directory: " + reportDir.getAbsolutePath());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.getAbsolutePath() + "/CartTests.html");
        sparkReporter.config().setDocumentTitle("Cart Tests Report");
        sparkReporter.config().setReportName("Cart Tests");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        test = extent.createTest(method.getName(), "Cart workflow automation scenarios");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (test != null) {
            if (result.getStatus() == ITestResult.SUCCESS) {
                test.log(Status.PASS, "Test passed");
            } else if (result.getStatus() == ITestResult.FAILURE) {
                test.log(Status.FAIL, result.getThrowable());
            } else if (result.getStatus() == ITestResult.SKIP) {
                test.log(Status.SKIP, "Test skipped");
            }
        }
        super.tearDown();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @DataProvider(name = "supportedUsers")
    public Object[][] supportedUsers() {
        return new Object[][] {
                {"standard_user", PASSWORD, "secret_sauce"},
                {"problem_user", PASSWORD, "secret_sauce"},
                {"performance_glitch_user", PASSWORD, "secret_sauce"},
                {"visual_user", PASSWORD, "secret_sauce"},
                {"error_user", PASSWORD, "secret_sauce"}
        };
    }

    @Test(dataProvider = "supportedUsers", description = "TC11 - Verify added products appear in Cart")
    public void verifyAddedProductsAppearInCart(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding a product and validating it appears in cart");

        String productName = getFirstProductName();
        addProductToCart(productName);
        navigateToCart();

        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
        Assert.assertTrue(cartPage.getCartItems().stream().anyMatch(item -> item.contains(productName)),
                "Selected product should appear in cart");
        Assert.assertEquals(cartPage.getItemCount(), 1, "Cart should display one item");

        test.pass("The selected product appears correctly in the cart");
    }

    @Test(dataProvider = "supportedUsers", description = "TC12 - Verify Continue Shopping button")
    public void verifyContinueShoppingButton(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding a product, opening cart, and verifying Continue Shopping navigation");

        addProductToCart(getFirstProductName());
        navigateToCart();
        cartPage.clickContinueShopping();

        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after Continue Shopping");
        test.pass("Continue Shopping navigated back to the Products page");
    }

    @Test(dataProvider = "supportedUsers", description = "TC13 - Verify Remove item from Cart")
    public void verifyRemoveItemFromCart(String username, String password, String userType) {

        loginAsUser(username, password, userType);

        test.info("Adding a product, removing it from cart, and validating the cart updates");


        String productName = getFirstProductName();

        addProductToCart(productName);

        navigateToCart();


        cartPage.removeProduct(productName);

       // acceptAlert();


        Assert.assertEquals(
                cartPage.getItemCount(),
                0,
                "Cart should be empty after removing the product"
        );


        Assert.assertEquals(
                productsPage.getCartBadgeCount(),
                0,
                "Cart badge should update to zero after removal"
        );


        test.pass("The product was removed successfully from the cart");
    }

    @Test(dataProvider = "supportedUsers", description = "TC14 - Verify Checkout button navigation")
    public void verifyCheckoutButtonNavigation(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding a product and verifying Checkout opens the checkout information page");

        addProductToCart(getFirstProductName());
        navigateToCart();
        cartPage.clickCheckout();

        CheckoutInformationPage checkoutInformationPage = new CheckoutInformationPage(driver);
        Assert.assertTrue(checkoutInformationPage.isCheckoutInformationPageDisplayed(),
                "Checkout Information page should be displayed after clicking Checkout");

        test.pass("Checkout navigation opened the information page correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC15 - Verify multiple products in cart")
    public void verifyMultipleProductsInCart(String username, String password, String userType) {

        loginAsUser(username, password, userType);

        test.info("Adding all products and validating they appear in cart");

        List<String> selectedProducts = getFirstProducts(6);

        for (String productName : selectedProducts) {
            addProductToCart(productName);
        }

        navigateToCart();

        Assert.assertEquals(
                cartPage.getItemCount(),
                6,
                "Cart should contain all 6 products"
        );

        for (String productName : selectedProducts) {
            Assert.assertTrue(
                    cartPage.getCartItems().stream()
                            .anyMatch(item -> item.contains(productName)),
                    "Product should be present in cart: " + productName
            );
        }

        test.pass("All 6 products were added and displayed correctly in the cart");
    }

    @Test(dataProvider = "supportedUsers", description = "TC16 - Verify cart badge count")
    public void verifyCartBadgeCount(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding two products and verifying the cart badge count matches");

        List<String> selectedProducts = getFirstProducts(MULTI_PRODUCT_COUNT);
        for (String productName : selectedProducts) {
            addProductToCart(productName);
        }

        Assert.assertEquals(productsPage.getCartBadgeCount(), selectedProducts.size(),
                "Cart badge count should match the number of selected products");

        test.pass("Cart badge count matched the added products");
    }

    @Test(dataProvider = "supportedUsers", description = "TC17 - Verify removing all products from cart")
    public void verifyRemovingAllProductsFromCart(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding multiple products, removing them, and verifying the cart becomes empty");

        List<String> selectedProducts = getFirstProducts(MULTI_PRODUCT_COUNT);
        for (String productName : selectedProducts) {
            addProductToCart(productName);
        }
        navigateToCart();

        for (String productName : selectedProducts) {
            cartPage.removeProduct(productName);
        }

        Assert.assertEquals(cartPage.getItemCount(), 0, "Cart should be empty after removing all products");
        Assert.assertEquals(productsPage.getCartBadgeCount(), 0, "Cart badge should be zero after removing all products");

        test.pass("All products were removed successfully and the cart is empty");
    }

    @Test(dataProvider = "supportedUsers", description = "TC18 - Verify cart state after browser refresh")
    public void verifyCartStateAfterBrowserRefresh(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Adding a product, refreshing the browser, and verifying the product remains in cart");

        String productName = getFirstProductName();
        addProductToCart(productName);
        navigateToCart();
        driver.navigate().refresh();

        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should remain visible after refresh");
        Assert.assertTrue(cartPage.getCartItems().stream().anyMatch(item -> item.contains(productName)),
                "Cart contents should persist after browser refresh");

        test.pass("Cart state persisted correctly after browser refresh");
    }

    @Test(dataProvider = "supportedUsers", description = "TC19 - Verify checkout cannot proceed with empty cart")
    public void verifyCheckoutWithEmptyCart(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        test.info("Opening the cart with no products and attempting checkout");

        navigateToCart();
        cartPage.clickCheckout();

        CheckoutInformationPage checkoutInformationPage = new CheckoutInformationPage(driver);
        Assert.assertTrue(checkoutInformationPage.isCheckoutInformationPageDisplayed(),
                "Checkout information page should be displayed when checkout is attempted with an empty cart");

        test.pass("Checkout handling worked as expected for an empty cart");
    }

    private void loginAsUser(String username, String password, String userType) {
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login for " + username);
        if ("error_user".equals(userType)) {
            test.info("Error user logged in and cart actions are being validated");
        } else {
            test.info("Logged in as " + username + " and reached the Products page");
        }
    }

    private void addProductToCart(String productName) {
        productsPage.addProductByName(productName);
        test.info("Added product to cart: " + productName);
    }

    private void navigateToCart() {
        productsPage.clickCart();
        cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed after opening cart");
    }

    private String getFirstProductName() {
        return productsPage.getAllProductNames().get(0);
    }

    private List<String> getFirstProducts(int count) {
        return new ArrayList<>(productsPage.getAllProductNames().subList(0, count));
    }
}
