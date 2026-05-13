package com.example.seleniumJava;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTest {

    private WebDriver driver;  

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        
        // REQUERIMIENTO: Tiempo de espera explícito
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginExitoso() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        
        assertTrue(driver.getCurrentUrl().contains("inventory"), "El login exitoso falló");
    }

    @Test
    public void testLoginFallido() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("usuario_invalido");
        driver.findElement(By.id("password")).sendKeys("contraseña_incorrecta");
        driver.findElement(By.id("login-button")).click();
        
        // Verificamos que seguimos en la página de login
        assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"), "El login fallido redirigió a otra página");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}