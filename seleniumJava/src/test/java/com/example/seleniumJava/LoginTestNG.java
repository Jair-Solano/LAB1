package com.example.seleniumJava;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert; 
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTestNG {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        // Mantenemos el requerimiento del tiempo de espera explícito
        //driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginTestNG() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        
        // Validación usando el Assert de TestNG
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "El login falló en TestNG");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}