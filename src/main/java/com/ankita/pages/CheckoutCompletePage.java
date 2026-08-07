package com.ankita.pages;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutCompletePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement checkoutCompleteTitle;

    @FindBy(className = "complete-header")
    private WebElement thankYouMessage;

    @FindBy(className = "complete-text")
    private WebElement completeMessage;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    @FindBy(className = "pony_express")
    private WebElement successImage;

    public CheckoutCompletePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isCheckoutCompleteDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(checkoutCompleteTitle)).isDisplayed();
    }

    public String getThankYouMessage() {
        return wait.until(ExpectedConditions.visibilityOf(thankYouMessage)).getText();
    }

    public String getCompleteMessage() {
        return wait.until(ExpectedConditions.visibilityOf(completeMessage)).getText();
    }

    public boolean isSuccessImageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(successImage)).isDisplayed();
    }

    public void clickBackHome() {
        wait.until(ExpectedConditions.elementToBeClickable(backHomeButton)).click();
    }
}
