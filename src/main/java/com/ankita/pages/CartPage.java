package com.ankita.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement cartTitle;

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(xpath = "//button[contains(@class,'btn_secondary') and text()='Remove']")
    private List<WebElement> removeButtons;

    @FindBy(className = "cart_quantity")
    private List<WebElement> quantityLabels;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public boolean isCartPageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(cartTitle)).isDisplayed();
    }

//    public int getItemCount() {
//        return cartItems.size();
//    }

    public int getItemCount() {
        return cartItems.size();
    }

    public List<String> getCartItems() {
        return cartItems.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }


    public void removeProduct(String productName) {
        By productRemoveButton = By.xpath("//div[@class='cart_item' and .//div[text()='" + productName + "']]//button[contains(@class,'btn_secondary') and text()='Remove']");
        wait.until(ExpectedConditions.elementToBeClickable(productRemoveButton)).click();
    }



    public void clickContinueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton)).click();
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }


}
