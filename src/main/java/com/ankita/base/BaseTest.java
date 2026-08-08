package com.ankita.base;

import com.ankita.utilities.ConfigReader;
import com.ankita.utilities.DriverFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.initializeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String url = ConfigReader.getProperty("url");
        driver.get(url);

        wait.until(ExpectedConditions.urlContains("saucedemo.com"));
    }

    public void acceptAlert() {
        WebDriverWait alertWait =
                new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = alertWait.until(
                ExpectedConditions.alertIsPresent()
        );

        alert.accept();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        driver = null;
        wait = null;
    }
}