package com.example.seleniumJava;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert; 
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.example.seleniumJava.pages.InventoryPage;
import com.example.seleniumJava.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTestNG {

    private WebDriver driver;
    private ExtentReports extentReports;
    private ExtentTest test;

    @BeforeClass
    public void setUpReport() {
        File reportDir = new File("Reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("Reports/extent-report.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
    }

    @BeforeMethod
    public void setUp() {
        // Configurar WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginTestNG() {
        test = extentReports.createTest("Login Test", "Validar login en SauceDemo");
        
        try {
            driver.get("https://www.saucedemo.com/");
            test.info("Navegado a SauceDemo");
            
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            test.info("Username ingresado");
            
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            test.info("Password ingresado");
            
            driver.findElement(By.id("login-button")).click();
            test.info("Botón de login clickeado");
            
            // Validación usando el Assert de TestNG
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "El login falló en TestNG");
            test.pass("Login exitoso - URL contiene 'inventory'");
        } catch (Exception e) {
            test.fail("Login falló: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testLoginAndAddProductToCart() {
        test = extentReports.createTest("Login + Add to Cart", "Login válido y agregar producto al carrito");
        
        try {
            driver.get("https://www.saucedemo.com/");
            test.info("Navegado a SauceDemo");

            LoginPage loginPage = new LoginPage(driver);
            InventoryPage inventoryPage = new InventoryPage(driver);

            loginPage.login("standard_user", "secret_sauce");
            test.info("Login ejecutado con usuario estándar");

            inventoryPage.addBackpackToCart();
            test.info("Agregar producto al carrito");
            test.pass("Se cumplió: Agregar producto al carrito");

            String count = inventoryPage.getCartCount();
            Assert.assertEquals(count, "1", "El carrito debería tener exactamente 1 producto.");
            test.pass("Se cumplió: Validar que el carrito tenga 1 producto");
        } catch (Exception e) {
            test.fail("La prueba de carrito falló: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}