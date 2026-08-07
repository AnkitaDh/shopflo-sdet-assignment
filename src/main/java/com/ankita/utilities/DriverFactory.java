package com.ankita.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver initializeDriver() {
        if (DRIVER.get() == null) {
            String browserName = ConfigReader.getProperty("browser", "chrome").trim().toLowerCase();

            WebDriver driver = createDriver(browserName);

            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();

            DRIVER.set(driver);
        }

        return DRIVER.get();
    }

    public static WebDriver getDriver() {
        return initializeDriver();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();

        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver createDriver(String browserName) {

        return switch (browserName) {
            case "chrome" -> createChromeDriver();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browserName
            );
        };
    }

    private static WebDriver createChromeDriver() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");

        /*
         * Disable Chrome Password Manager features for the
         * automation browser session.
         *
         * This prevents Chrome's password breach warning from
         * interfering with Selenium test execution.
         */
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));

        return new ChromeDriver(options);
    }
}