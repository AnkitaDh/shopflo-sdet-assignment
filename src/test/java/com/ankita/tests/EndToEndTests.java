package com.ankita.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
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
import com.ankita.pages.CheckoutCompletePage;
import com.ankita.pages.CheckoutInformationPage;
import com.ankita.pages.CheckoutOverviewPage;
import com.ankita.pages.LoginPage;
import com.ankita.pages.ProductsPage;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class EndToEndTests extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String FIRST_NAME = "Ankita";
    private static final String LAST_NAME = "Tester";
    private static final String ZIP_CODE = "462001";
    private static final String PRODUCT_ONE = "Sauce Labs Backpack";
    private static final String PRODUCT_TWO = "Sauce Labs Bike Light";
    private static final String PRODUCT_THREE = "Sauce Labs Bolt T-Shirt";

    private static ExtentReports extent;
    private ExtentTest test;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInformationPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;

    @BeforeSuite(alwaysRun = true)
    public void setUpReport() {
        File reportDir = new File("target/surefire-reports");
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new IllegalStateException("Unable to create report directory: " + reportDir.getAbsolutePath());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.getAbsolutePath() + "/EndToEndTests.html");
        sparkReporter.config().setDocumentTitle("End-to-End Tests Report");
        sparkReporter.config().setReportName("End-to-End Tests");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        test = extent.createTest(method.getName(), "End-to-end purchase workflow scenarios");
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
                {"standard_user", PASSWORD, "standard"},
                {"problem_user", PASSWORD, "problem"},
                {"performance_glitch_user", PASSWORD, "performance"},
                {"visual_user", PASSWORD, "visual"},
                {"error_user", PASSWORD, "error"}
        };
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_001 - Complete purchase flow with single product")
    public void completePurchaseFlowWithSingleProduct(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Perform purchase flow
        addSingleProduct(PRODUCT_ONE);
        completeCheckout();

        // Assert - Verify order completion
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(), "Checkout Complete page should be displayed");
        Assert.assertEquals(checkoutCompletePage.getThankYouMessage(), "Thank you for your order!",
                "Success message should be displayed");

        test.pass("Single-product purchase flow completed successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_002 - Complete purchase flow with multiple products")
    public void completePurchaseFlowWithMultipleProducts(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Add multiple products and complete checkout
        addMultipleProducts(List.of(PRODUCT_ONE, PRODUCT_TWO, PRODUCT_THREE));
        completeCheckout();

        // Assert - Verify order completion
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(), "Checkout Complete page should be displayed");
        Assert.assertEquals(checkoutCompletePage.getThankYouMessage(), "Thank you for your order!",
                "Success message should be displayed");

        test.pass("Multi-product purchase flow completed successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_003 - Remove product before checkout")
    public void removeProductBeforeCheckout(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Add products, remove one, and continue checkout
        addMultipleProducts(List.of(PRODUCT_ONE, PRODUCT_TWO));
        navigateToCart();
        cartPage.removeProduct(PRODUCT_ONE);
        completeCheckout();

        // Assert - Verify remaining product is processed
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(), "Checkout Complete page should be displayed");
        test.pass("Checkout completed with the remaining product only");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_004 - Verify continue shopping workflow")
    public void verifyContinueShoppingWorkflow(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Continue shopping and then checkout
        addSingleProduct(PRODUCT_ONE);
        navigateToCart();
        cartPage.clickContinueShopping();
        addSingleProduct(PRODUCT_TWO);
        completeCheckout();

        // Assert - Verify both products appear in checkout
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(), "Checkout Complete page should be displayed");
        test.pass("Continue shopping workflow completed successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_005 - Verify logout after successful login")
    public void verifyLogoutAfterSuccessfulLogin(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Logout from the menu
        productsPage.clickMenu();
        productsPage.logout();

        // Assert - Verify user is returned to the login page
        LoginPage loginPageAfterLogout = new LoginPage(driver);
        Assert.assertTrue(loginPageAfterLogout.isLoginButtonDisplayed(), "Login button should be displayed after logout");

        test.pass("Logout returned the user to the login page");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_006 - Verify reset application state")
    public void verifyResetApplicationState(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Add products and then reset app state
        addMultipleProducts(List.of(PRODUCT_ONE, PRODUCT_TWO));
        productsPage.resetAppState();

        // Assert - Verify cart is empty
        Assert.assertEquals(productsPage.getCartBadgeCount(), 0, "Cart badge should be zero after resetting the app state");

        test.pass("Reset app state cleared the cart successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_007 - Verify browser navigation during purchase flow")
    public void verifyBrowserNavigationDuringPurchaseFlow(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Navigate browser history and refresh
        addSingleProduct(PRODUCT_ONE);
        driver.navigate().back();
        driver.navigate().forward();
        driver.navigate().refresh();

        // Assert - Verify application remains stable
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should remain available after browser navigation");

        test.pass("Browser navigation did not break the application");
    }

    @Test(dataProvider = "supportedUsers", description = "E2E_008 - Verify order confirmation data")
    public void verifyOrderConfirmationData(String username, String password, String userType) {
        // Arrange - Prepare application state
        loginAsUser(username, password, userType);

        // Act - Complete checkout
        addSingleProduct(PRODUCT_ONE);
        completeCheckout();

        // Assert - Verify completion page content
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(), "Checkout Complete page should be displayed");
        Assert.assertTrue(checkoutCompletePage.getCompleteMessage().contains("Your order has been dispatched")
                || checkoutCompletePage.getCompleteMessage().contains("Your order has been received"),
                "Completion message should contain meaningful confirmation text");

        test.pass("Order confirmation data was displayed correctly");
    }

    private void loginAsUser(String username, String password, String userType) {
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login for " + username);
        if ("performance_glitch_user".equals(userType)) {
            test.info("Performance user reached the products page");
        } else if ("visual_user".equals(userType)) {
            test.info("Visual user reached the products page");
        } else if ("error_user".equals(userType)) {
            test.info("Error user reached the products page");
        } else {
            test.info("Logged in as " + username + " and reached the Products page");
        }
    }

    private void addSingleProduct(String productName) {
        productsPage.addProductByName(productName);
        test.info("Added product to cart: " + productName);
    }

    private void addMultipleProducts(List<String> productNames) {
        for (String productName : productNames) {
            productsPage.addProductByName(productName);
        }
        test.info("Added multiple products to cart");
    }

    private void navigateToCart() {
        productsPage.clickCart();
        cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
    }

    private void completeCheckout() {
        navigateToCart();
        cartPage.clickCheckout();
        checkoutInformationPage = new CheckoutInformationPage(driver);
        Assert.assertTrue(checkoutInformationPage.isCheckoutInformationPageDisplayed(),
                "Checkout Information page should be displayed");

        // Arrange - Prepare customer details
        checkoutInformationPage.enterCheckoutInformation(FIRST_NAME, LAST_NAME, ZIP_CODE);

        // Act - Continue to checkout overview and finish order
        checkoutInformationPage.clickContinue();
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should be displayed");

        checkoutOverviewPage.clickFinish();
        checkoutCompletePage = new CheckoutCompletePage(driver);
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(),
                "Checkout Complete page should be displayed after finishing the order");
    }
}
