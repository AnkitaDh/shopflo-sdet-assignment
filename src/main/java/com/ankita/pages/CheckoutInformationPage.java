package com.ankita.pages;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutInformationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "first-name")
    private WebElement firstNameTextBox;

    @FindBy(id = "last-name")
    private WebElement lastNameTextBox;

    @FindBy(id = "postal-code")
    private WebElement zipCodeTextBox;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(xpath = "//h3[@data-test='error']")
    private WebElement errorMessage;

    public CheckoutInformationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(firstNameTextBox)).clear();
        firstNameTextBox.sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        wait.until(ExpectedConditions.visibilityOf(lastNameTextBox)).clear();
        lastNameTextBox.sendKeys(lastName);
    }

    public void enterZipCode(String zipCode) {
        wait.until(ExpectedConditions.visibilityOf(zipCodeTextBox)).clear();
        zipCodeTextBox.sendKeys(zipCode);
    }

    public void enterCheckoutInformation(String firstName, String lastName, String zipCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterZipCode(zipCode);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText();
    }

    public boolean isCheckoutInformationPageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(firstNameTextBox)).isDisplayed();
    }
}
