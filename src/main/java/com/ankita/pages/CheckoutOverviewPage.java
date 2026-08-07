package com.ankita.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutOverviewPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement checkoutOverviewTitle;

    @FindBy(className = "summary_value_label")
    private List<WebElement> paymentInformation;

    @FindBy(xpath = "//div[contains(@class,'summary_info') and contains(.,'FREE PONY EXPRESS DELIVERY')]")
    private WebElement shippingInformation;

    @FindBy(className = "summary_subtotal_label")
    private WebElement itemTotal;

    @FindBy(className = "summary_tax_label")
    private WebElement tax;

    @FindBy(className = "summary_total_label")
    private WebElement totalPrice;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(className = "inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(className = "inventory_item_price")
    private List<WebElement> productPrices;

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isCheckoutOverviewDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(checkoutOverviewTitle)).isDisplayed();
    }

    public String getPaymentInformation() {
        return wait.until(ExpectedConditions.visibilityOfAllElements(paymentInformation)).get(0).getText();
    }

    public String getShippingInformation() {
        return wait.until(ExpectedConditions.visibilityOf(shippingInformation)).getText();
    }

    public String getItemTotal() {
        return wait.until(ExpectedConditions.visibilityOf(itemTotal)).getText();
    }

    public String getTax() {
        return wait.until(ExpectedConditions.visibilityOf(tax)).getText();
    }

    public String getTotalPrice() {
        return wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText();
    }

    public void clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
    }

    public List<String> getProductNames() {
        wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
        return productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getProductPrices() {
        wait.until(ExpectedConditions.visibilityOfAllElements(productPrices));
        return productPrices.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
