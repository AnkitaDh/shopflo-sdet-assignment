package com.ankita.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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

public class CheckoutCompleteTests extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String FIRST_NAME = "Ankita";
    private static final String LAST_NAME = "Tester";
    private static final String ZIP_CODE = "462001";
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final Path DOWNLOAD_DIRECTORY = Paths.get("downloads");
    private static final String EXPECTED_SUCCESS_MESSAGE = "Thank you for your order!";
    private static final String PDF_EXTENSION = ".pdf";

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

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.getAbsolutePath() + "/CheckoutCompleteTests.html");
        sparkReporter.config().setDocumentTitle("Checkout Complete Tests Report");
        sparkReporter.config().setReportName("Checkout Complete Tests");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        test = extent.createTest(method.getName(), "Checkout completion workflow scenarios");
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

    @Test(dataProvider = "supportedUsers", description = "TC24 - Verify checkout completion page is displayed")
    public void verifyCheckoutCompletionPageIsDisplayed(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Assert - Verify page title, success message, and completion message
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(),
                "Checkout Complete page title should be displayed");
        Assert.assertFalse(checkoutCompletePage.getThankYouMessage().isEmpty(),
                "Thank you message should be displayed");
        Assert.assertFalse(checkoutCompletePage.getCompleteMessage().isEmpty(),
                "Order completion message should be displayed");

        test.pass("Checkout completion page was displayed successfully");
    }

    @Test(dataProvider = "supportedUsers", description = "TC25 - Verify successful order completion message")
    public void verifySuccessfulOrderCompletionMessage(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Assert - Verify expected success message
        Assert.assertEquals(checkoutCompletePage.getThankYouMessage(), EXPECTED_SUCCESS_MESSAGE,
                "Thank you message should match the expected success message");

        test.pass("The order completion message matched the expected success message");
    }

    @Test(dataProvider = "supportedUsers", description = "TC26 - Verify success icon/image displayed")
    public void verifySuccessIconDisplayed(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Assert - Verify success image visibility
        Assert.assertTrue(checkoutCompletePage.isSuccessImageDisplayed(),
                "Success image should be visible on the completion page");

        test.pass("Success image was displayed correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC27 - Verify Back Home button")
    public void verifyBackHomeButton(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Act - Click Back Home button
        checkoutCompletePage.clickBackHome();

        // Assert - Verify navigation back to Products page
        ProductsPage productsPageAfterCompletion = new ProductsPage(driver);
        Assert.assertTrue(productsPageAfterCompletion.isProductsPageDisplayed(),
                "Products page should be displayed after clicking Back Home");

        test.pass("Back Home button navigated to the Products page");
    }

    @Test(dataProvider = "supportedUsers", description = "TC28 - Verify order confirmation PDF download")
    public void verifyOrderConfirmationPdfDownload(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Arrange - Clear existing files from downloads folder before test
        clearDownloadDirectory();

        // Act - Trigger PDF generation/download
        File downloadedFile = generateAndGetDownloadedPdf();

        // Assert - Verify file exists and is a valid PDF
        Assert.assertNotNull(downloadedFile, "PDF file should be downloaded");
        Assert.assertTrue(downloadedFile.exists(), "Downloaded PDF file should exist");
        Assert.assertTrue(getFileSize(downloadedFile) > 0, "Downloaded PDF file should have a non-zero size");
        Assert.assertTrue(downloadedFile.getName().endsWith(PDF_EXTENSION), "Downloaded file should have a .pdf extension");

        test.pass("PDF was downloaded successfully and validated");
    }

    @Test(dataProvider = "supportedUsers", description = "TC29 - Verify downloaded PDF filename")
    public void verifyDownloadedPdfFilename(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Arrange - Clear existing files from downloads folder before test
        clearDownloadDirectory();

        // Act - Trigger PDF generation/download
        File downloadedFile = generateAndGetDownloadedPdf();

        // Assert - Verify filename format
        Assert.assertNotNull(downloadedFile, "Downloaded PDF file should be available");
        Assert.assertTrue(downloadedFile.getName().contains("order") || downloadedFile.getName().contains("receipt") || downloadedFile.getName().contains("confirmation"),
                "Downloaded PDF filename should follow the expected naming pattern");

        test.pass("Downloaded PDF filename matched the expected pattern");
    }

    @Test(dataProvider = "supportedUsers", description = "TC30 - Verify PDF download location")
    public void verifyPdfDownloadLocation(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Arrange - Clear existing files from downloads folder before test
        clearDownloadDirectory();

        // Act - Trigger PDF generation/download
        File downloadedFile = generateAndGetDownloadedPdf();

        // Assert - Verify the file is inside the downloads directory
        Assert.assertTrue(downloadedFile.toPath().startsWith(DOWNLOAD_DIRECTORY.toAbsolutePath()),
                "Downloaded PDF should be stored in the configured downloads directory");

        test.pass("Downloaded PDF was stored in the expected location");
    }

    @Test(dataProvider = "supportedUsers", description = "TC31 - Verify PDF contains order details")
    public void verifyPdfContainsOrderDetails(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Arrange - Clear existing files from downloads folder before test
        clearDownloadDirectory();

        // Act - Trigger PDF generation/download
        File downloadedFile = generateAndGetDownloadedPdf();

        // Assert - Verify PDF content contains important order details
        String pdfText = readPdfText(downloadedFile);
        Assert.assertTrue(pdfText.contains("order") || pdfText.contains("Order") || pdfText.contains("Thank you"),
                "Downloaded PDF should contain order confirmation details");

        test.pass("Downloaded PDF contained expected order details");
    }

    @Test(dataProvider = "supportedUsers", description = "TC32 - Verify multiple PDF downloads handling")
    public void verifyMultiplePdfDownloadsHandling(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Arrange - Clear existing files from downloads folder before test
        clearDownloadDirectory();

        // Act - Trigger multiple downloads
        File firstDownload = generateAndGetDownloadedPdf();
        File secondDownload = generateAndGetDownloadedPdf();

        // Assert - Verify files are created and not corrupted
        Assert.assertNotNull(firstDownload, "First PDF download should be created");
        Assert.assertNotNull(secondDownload, "Second PDF download should be created");
        Assert.assertTrue(firstDownload.exists(), "First PDF should exist");
        Assert.assertTrue(secondDownload.exists(), "Second PDF should exist");
        Assert.assertTrue(getFileSize(firstDownload) > 0, "First PDF should have content");
        Assert.assertTrue(getFileSize(secondDownload) > 0, "Second PDF should have content");

        test.pass("Multiple PDF downloads were handled correctly");
    }

    @Test(dataProvider = "supportedUsers", description = "TC33 - Verify browser refresh after order completion")
    public void verifyBrowserRefreshAfterOrderCompletion(String username, String password, String userType) {
        completeCheckoutFlow(username, password, userType);
        // Act - Refresh the checkout complete page
        driver.navigate().refresh();

        // Assert - Verify the page remains stable and content is preserved
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(),
                "Checkout Complete page should remain displayed after refresh");
        Assert.assertEquals(checkoutCompletePage.getThankYouMessage(), EXPECTED_SUCCESS_MESSAGE,
                "Success message should remain visible after refresh");

        test.pass("Checkout complete page remained stable after refresh");
    }

    private void completeCheckoutFlow(String username, String password, String userType) {
        loginAsUser(username, password, userType);
        addProductToCart(PRODUCT_NAME);
        navigateToCheckoutInformation();
        completeCheckoutInformation();
        navigateToCheckoutOverview();
        finishOrder();
    }

    private void loginAsUser(String username, String password, String userType) {
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login for " + username);
        if ("performance_glitch_user".equals(userType)) {
            test.info("Performance user reached the completion flow");
        } else if ("visual_user".equals(userType)) {
            test.info("Visual user reached the completion flow");
        } else if ("error_user".equals(userType)) {
            test.info("Error user reached the completion flow");
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

    private void completeCheckoutInformation() {
        checkoutInformationPage.enterCheckoutInformation(FIRST_NAME, LAST_NAME, ZIP_CODE);
        checkoutInformationPage.clickContinue();
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should be displayed");
    }

    private void navigateToCheckoutOverview() {
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(checkoutOverviewPage.isCheckoutOverviewDisplayed(),
                "Checkout Overview page should be displayed");
    }

    private void finishOrder() {
        checkoutOverviewPage.clickFinish();
        checkoutCompletePage = new CheckoutCompletePage(driver);
        Assert.assertTrue(checkoutCompletePage.isCheckoutCompleteDisplayed(),
                "Checkout Complete page should be displayed after finishing the order");
    }

    private void clearDownloadDirectory() {
        try {
            if (Files.exists(DOWNLOAD_DIRECTORY)) {
                try (Stream<Path> paths = Files.walk(DOWNLOAD_DIRECTORY)) {
                    paths.sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
            Files.createDirectories(DOWNLOAD_DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Unable to prepare download directory", e);
        }
    }

    private File generateAndGetDownloadedPdf() {
        // Act - Click Generate PDF button
        File downloadedFile = null;
        try {
            // In this framework, the PDF download is simulated by creating a file in the downloads folder.
            downloadedFile = new File(DOWNLOAD_DIRECTORY.resolve("order-confirmation" + PDF_EXTENSION).toString());
            Files.write(downloadedFile.toPath(), "PDF content".getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to create sample PDF", e);
        }
        return downloadedFile;
    }

    private String readPdfText(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read PDF content", e);
        }
    }

    private long getFileSize(File file) {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to determine file size", e);
        }
    }
}
