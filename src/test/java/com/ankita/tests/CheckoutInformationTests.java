package com.ankita.tests;

import java.io.File;
import java.lang.reflect.Method;

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
import com.ankita.pages.CheckoutOverviewPage;
import com.ankita.pages.LoginPage;
import com.ankita.pages.ProductsPage;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class CheckoutInformationTests extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String FIRST_NAME = "Ankita";
    private static final String LAST_NAME = "Tester";
    private static final String ZIP_CODE = "462001";

    private static ExtentReports extent;
    private ExtentTest test;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInformationPage;

    @BeforeSuite(alwaysRun = true)
    public void setUpReport() {
        File reportDir = new File("target/surefire-reports");
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new IllegalStateException("Unable to create report directory: " + reportDir.getAbsolutePath());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.getAbsolutePath() + "/CheckoutInformationTests.html");
        sparkReporter.config().setDocumentTitle("Checkout Information Tests Report");
        sparkReporter.config().setReportName("Checkout Information Tests");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        test = extent.createTest(method.getName(), "Checkout information workflow scenarios");
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

    @Test(dataProvider = "supportedUsers", description = "TC15 - Verify successful checkout information submission")
    public void verifySuccessfulCheckoutInformationSubmission(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Enter valid checkout information and continue
        checkoutInformationPage.enterCheckoutInformation(FIRST_NAME, LAST_NAME, ZIP_CODE);
        checkoutInformationPage.clickContinue();

        // Assert - Verify checkout overview page is displayed
        CheckoutOverviewPage checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should be displayed after valid submission");

        test.pass("Checkout information was accepted and overview page displayed");
    }

    @Test(dataProvider = "supportedUsers", description = "TC16 - Verify validation when First Name is blank")
    public void verifyValidationWhenFirstNameIsBlank(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Leave first name blank and continue
        checkoutInformationPage.enterLastName(LAST_NAME);
        checkoutInformationPage.enterZipCode(ZIP_CODE);
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation message is shown
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed when first name is blank");

        test.pass("Validation message appeared for blank first name");
    }

    @Test(dataProvider = "supportedUsers", description = "TC17 - Verify validation when Last Name is blank")
    public void verifyValidationWhenLastNameIsBlank(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Leave last name blank and continue
        checkoutInformationPage.enterFirstName(FIRST_NAME);
        checkoutInformationPage.enterZipCode(ZIP_CODE);
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation message is shown
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed when last name is blank");

        test.pass("Validation message appeared for blank last name");
    }

    @Test(dataProvider = "supportedUsers", description = "TC18 - Verify validation when Zip Code is blank")
    public void verifyValidationWhenZipCodeIsBlank(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Leave zip code blank and continue
        checkoutInformationPage.enterFirstName(FIRST_NAME);
        checkoutInformationPage.enterLastName(LAST_NAME);
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation message is shown
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed when zip code is blank");

        test.pass("Validation message appeared for blank zip code");
    }

    @Test(dataProvider = "supportedUsers", description = "TC19 - Verify validation when all checkout fields are empty")
    public void verifyValidationWhenAllFieldsAreEmpty(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Click continue without filling any fields
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation message is shown
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed when all fields are empty");

        test.pass("Validation message appeared for empty checkout form");
    }

    @Test(dataProvider = "supportedUsers", description = "TC20 - Verify invalid Zip Code handling")
    public void verifyInvalidZipCodeHandling(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Enter invalid zip code and continue
        checkoutInformationPage.enterCheckoutInformation(FIRST_NAME, LAST_NAME, "abc123");
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation/error handling
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed for invalid zip code");

        test.pass("Invalid zip code was handled correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC21 - Verify special characters in First Name")
    public void verifySpecialCharactersInFirstName(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Enter special characters in first name and continue
        checkoutInformationPage.enterCheckoutInformation("@Test123", LAST_NAME, ZIP_CODE);
        checkoutInformationPage.clickContinue();

        // Assert - Verify validation/error handling
        String errorMessage = checkoutInformationPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Validation message should be displayed for special characters in first name");

        test.pass("Special characters in first name were handled correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC22 - Verify long input values")
    public void verifyLongInputValues(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Enter very long values and continue
        String longFirstName = "Ankita".repeat(50);
        String longLastName = "Tester".repeat(50);
        checkoutInformationPage.enterCheckoutInformation(longFirstName, longLastName, ZIP_CODE);
        checkoutInformationPage.clickContinue();

        // Assert - Application should handle input correctly
        CheckoutOverviewPage checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed() || !checkoutInformationPage.getErrorMessage().isEmpty(),
                "Application should handle long input values without crashing");

        test.pass("Long input values were handled correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC23 - Verify Cancel button")
    public void verifyCancelButtonNavigation(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart("Sauce Labs Backpack");
        navigateToCheckoutInformation();
        // Act - Click cancel
        checkoutInformationPage.clickCancel();

        // Assert - Verify navigation back to cart page
        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed after clicking Cancel");

        test.pass("Cancel button navigated back to the cart page");
    }

    private void loginAsUser(String username, String password, String userType) {
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login for " + username);
        if ("error_user".equals(userType)) {
            test.info("Error user logged in and checkout flow is being validated");
        } else {
            test.info("Logged in as " + username + " and reached the Products page");
        }
    }

    private void addProductToCart(String productName) {
        productsPage.addProductByName(productName);
        test.info("Added product to cart: " + productName);
    }

    private void navigateToCheckoutInformation() {
        productsPage.clickCart();
        cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");

        cartPage.clickCheckout();
        checkoutInformationPage = new CheckoutInformationPage(driver);
        Assert.assertTrue(checkoutInformationPage.isCheckoutInformationPageDisplayed(),
                "Checkout Information page should be displayed");
    }
}
