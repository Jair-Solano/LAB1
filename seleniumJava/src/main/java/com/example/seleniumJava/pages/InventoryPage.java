package com.example.seleniumJava.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage {
    WebDriver driver;

    // Selector para agregar el primer producto (Sauce Labs Backpack)
    By addToCartButton = By.id("add-to-cart-sauce-labs-backpack");
    // Selector del contador del carrito
    By cartBadge = By.className("shopping_cart_badge");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addBackpackToCart() {
        driver.findElement(addToCartButton).click();
    }

    public String getCartCount() {
        return driver.findElement(cartBadge).getText();
    }
}