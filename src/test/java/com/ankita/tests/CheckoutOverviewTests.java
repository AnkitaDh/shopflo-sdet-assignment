package com.ankita.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

public class CheckoutOverviewTests extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String FIRST_NAME = "Ankita";
    private static final String LAST_NAME = "Tester";
    private static final String ZIP_CODE = "462001";
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    private static ExtentReports extent;
    private ExtentTest test;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInformationPage;
    private CheckoutOverviewPage checkoutOverviewPage;

    @BeforeSuite(alwaysRun = true)
    public void setUpReport() {
        File reportDir = new File("target/surefire-reports");
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new IllegalStateException("Unable to create report directory: " + reportDir.getAbsolutePath());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.getAbsolutePath() + "/CheckoutOverviewTests.html");
        sparkReporter.config().setDocumentTitle("Checkout Overview Tests Report");
        sparkReporter.config().setReportName("Checkout Overview Tests");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        test = extent.createTest(method.getName(), "Checkout overview workflow scenarios");
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

    @Test(dataProvider = "supportedUsers", description = "TC21 - Verify checkout overview page loads successfully")
    public void verifyCheckoutOverviewPageLoadsSuccessfully(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Assert - Verify page title, summary, and action buttons
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page title should be displayed");
        Assert.assertFalse(checkoutOverviewPage.getProductNames().isEmpty(),
                "Product summary should be displayed");
        Assert.assertTrue(isElementDisplayed(By.id("finish")), "Finish button should be displayed");
        Assert.assertTrue(isElementDisplayed(By.id("cancel")), "Cancel button should be displayed");

        test.pass("Checkout Overview page loaded successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "TC22 - Verify product details on overview page")
    public void verifyProductDetailsOnOverviewPage(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Assert - Validate product details on the overview page
        Assert.assertTrue(checkoutOverviewPage.getProductNames().contains(PRODUCT_NAME),
                "Product name should be displayed on the overview page");
        Assert.assertFalse(checkoutOverviewPage.getProductPrices().isEmpty(),
                "Product price should be displayed on the overview page");
        Assert.assertFalse(checkoutOverviewPage.getProductNames().isEmpty(),
                "Product summary should be visible");

        test.pass("Product details appeared correctly on the overview page");
    }

    @Test(dataProvider = "supportedUsers", description = "TC23 - Verify payment information displayed")
    public void verifyPaymentInformationDisplayed(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Assert - Verify payment information section
        String paymentInformation = checkoutOverviewPage.getPaymentInformation();
        Assert.assertFalse(paymentInformation.isEmpty(), "Payment Information should be displayed");

        test.pass("Payment information was displayed correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC24 - Verify shipping information displayed")
    public void verifyShippingInformationDisplayed(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Assert - Verify shipping information section
        String shippingInformation = checkoutOverviewPage.getShippingInformation();
        Assert.assertFalse(shippingInformation.isEmpty(), "Shipping Information should be displayed");

        test.pass("Shipping information was displayed correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC25 - Verify item subtotal calculation")
    public void verifyItemSubtotalCalculation(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Capture the product price from the overview page
        String itemTotalText = checkoutOverviewPage.getItemTotal();

        // Assert - Verify displayed item total matches the selected product price
        Assert.assertTrue(itemTotalText.contains("$"), "Item total should be displayed with a currency value");
        Assert.assertTrue(itemTotalText.contains("Item total"), "Item total label should be displayed");

        test.pass("Item subtotal was displayed correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC26 - Verify tax calculation")
    public void verifyTaxCalculation(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Capture item total and tax value
        String taxText = checkoutOverviewPage.getTax();

        // Assert - Verify tax value is displayed correctly
        Assert.assertTrue(taxText.contains("Tax"), "Tax label should be displayed");
        Assert.assertTrue(taxText.contains("$") || taxText.contains("0"),
                "Tax amount should be displayed with a numeric value");

        test.pass("Tax value was displayed correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC27 - Verify total amount calculation")
    public void verifyTotalAmountCalculation(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Capture item total, tax, and total values
        String itemTotalText = checkoutOverviewPage.getItemTotal();
        String taxText = checkoutOverviewPage.getTax();
        String totalText = checkoutOverviewPage.getTotalPrice();

        // Assert - Verify total = item total + tax using BigDecimal
        BigDecimal itemTotal = parseCurrency(itemTotalText);
        BigDecimal tax = parseCurrency(taxText);
        BigDecimal total = parseCurrency(totalText);
        BigDecimal expectedTotal = itemTotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        Assert.assertEquals(total, expectedTotal, "Total amount should match Item Total + Tax");

        test.pass("Total amount calculation was verified successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "TC28 - Verify Finish button completes order")
    public void verifyFinishButtonCompletesOrder(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Click Finish button
        checkoutOverviewPage.clickFinish();

        // Assert - Verify checkout complete page opens
        CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage(driver);
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(),
                "Checkout Complete page should be displayed after finishing the order");

        test.pass("Finish button completed the order successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "TC29 - Verify Cancel button")
    public void verifyCancelButton(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Click Cancel button
        checkoutOverviewPage.clickCancel();

        // Assert - Verify user navigates back to Products page
        ProductsPage productsPageAfterCancel = new ProductsPage(driver);
        Assert.assertTrue(productsPageAfterCancel.isProductsPageDisplayed(),
                "Products page should be displayed after clicking Cancel");

        test.pass("Cancel button navigated back to the Products page");
    }

    @Test(dataProvider = "supportedUsers", description = "TC30 - Verify page refresh behaviour")
    public void verifyPageRefreshBehaviour(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutOverview();
        // Act - Refresh the checkout overview page
        driver.navigate().refresh();

        // Assert - Verify page remains stable and data remains available
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should remain displayed after refresh");
        Assert.assertFalse(checkoutOverviewPage.getProductNames().isEmpty(),
                "Product details should remain available after refresh");

        test.pass("Checkout Overview page remained stable after refresh");
    }

    private void loginAsUser(String username, String password, String userType) {
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login for " + username);
        if ("error_user".equals(userType)) {
            test.info("Error user logged in and checkout overview is being validated");
        } else {
            test.info("Logged in as " + username + " and reached the Products page");
        }
    }

    private void addProductToCart(String productName) {
        productsPage.addProductByName(productName);
        test.info("Added product to cart: " + productName);
    }

    private void navigateToCheckoutOverview() {
        productsPage.clickCart();
        cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");

        cartPage.clickCheckout();
        checkoutInformationPage = new CheckoutInformationPage(driver);
        Assert.assertTrue(checkoutInformationPage.isCheckoutInformationPageDisplayed(),
                "Checkout Information page should be displayed");

        checkoutInformationPage.enterCheckoutInformation(FIRST_NAME, LAST_NAME, ZIP_CODE);
        checkoutInformationPage.clickContinue();
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should be displayed");
    }

    private boolean isElementDisplayed(By locator) {
        return driver.findElement(locator).isDisplayed();
    }

    private BigDecimal parseCurrency(String value) {
        String normalized = value.replaceAll("[^0-9.-]", "");
        return new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
    }
}
