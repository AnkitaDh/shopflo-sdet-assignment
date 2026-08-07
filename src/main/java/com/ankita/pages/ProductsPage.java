package com.ankita.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement productsTitle;

    @FindBy(className = "shopping_cart_link")
    private WebElement shoppingCartIcon;

    @FindBy(className = "shopping_cart_badge")
    private WebElement shoppingCartBadge;

    @FindBy(xpath = "//button[contains(@class,'btn_inventory') and text()='Add to cart']")
    private List<WebElement> addToCartButtons;

    @FindBy(xpath = "//button[contains(@class,'btn_inventory') and text()='Remove']")
    private List<WebElement> removeButtons;

    @FindBy(className = "product_sort_container")
    private WebElement sortDropdown;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenu;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutOption;

    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppStateOption;

    @FindBy(className = "inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "inventory_item_price")
    private List<WebElement> productPrices;

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public boolean isProductsPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void addProductByName(String productName) {
        WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='inventory_item' and .//div[text()='" + productName + "']]//button[contains(@class,'btn_inventory') and text()='Add to cart']")));
        addButton.click();
    }

    public void removeProductByName(String productName) {
        WebElement removeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='inventory_item' and .//div[text()='" + productName + "']]//button[contains(@class,'btn_inventory') and text()='Remove']")));
        removeButton.click();
    }

    public void addAllProducts() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//button[contains(@class,'btn_inventory') and text()='Add to cart']"), 0));
        for (WebElement button : addToCartButtons) {
            if (button.isDisplayed()) {
                button.click();
            }
        }
    }

    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(
                By.className("shopping_cart_badge")
        );

        if (badges.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(badges.get(0).getText());
    }

    public void clickCart() {
        wait.until(ExpectedConditions.elementToBeClickable(shoppingCartIcon)).click();
    }

    public void sortByNameAZ() {
        selectSortOption("az");
    }

    public void sortByNameZA() {
        selectSortOption("za");
    }

    public void sortByPriceLowHigh() {
        selectSortOption("lohi");
    }

    public void sortByPriceHighLow() {
        selectSortOption("hilo");
    }

    public void clickMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(hamburgerMenu)).click();
    }

    public void logout() {
        clickMenu();
        wait.until(ExpectedConditions.elementToBeClickable(logoutOption)).click();
    }

    public void resetAppState() {
        clickMenu();
        wait.until(ExpectedConditions.elementToBeClickable(resetAppStateOption)).click();
    }

    public List<String> getAllProductNames() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
        } catch (UnhandledAlertException e) {
            dismissAlertIfPresent();
            wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
        }
        return productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getAllProductPrices() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(productPrices));
        } catch (UnhandledAlertException e) {
            dismissAlertIfPresent();
            wait.until(ExpectedConditions.visibilityOfAllElements(productPrices));
        }
        return productPrices.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    private void selectSortOption(String value) {
        wait.until(ExpectedConditions.visibilityOf(sortDropdown));
        Select select = new Select(sortDropdown);
        select.selectByValue(value);
        dismissAlertIfPresent();
    }

    private void dismissAlertIfPresent() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {
            // No alert present, continue with the flow.
        } catch (UnhandledAlertException ignored) {
            // Selenium may surface alerts as unhandled; try again on the active alert.
            try {
                Alert alert = driver.switchTo().alert();
                alert.accept();
            } catch (NoAlertPresentException ignoredAgain) {
                // Ignore if no alert is available.
            }
        }
    }
}
