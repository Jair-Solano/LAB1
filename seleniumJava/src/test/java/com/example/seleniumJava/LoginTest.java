package com.example.seleniumJava;

import java.io.File;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTest {

    private static ExtentReports extentReports;
    private ExtentTest test;
    private WebDriver driver;  

    @BeforeAll
    public static void initReport() {
        File reportDir = new File("Reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("Reports/extent-report.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
    }

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        
        // Configuración de timeout de carga de página
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
        driver.manage().window().maximize();
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testLoginExitoso() {
        test = extentReports.createTest("Login exitoso", "Validar login exitoso con credenciales válidas");

        driver.get("https://www.saucedemo.com/");
        test.info("Navegado a SauceDemo");

        WebDriverWait wait = getWait();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        test.info("Usuario ingresado");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        test.info("Contraseña ingresada");
        driver.findElement(By.id("login-button")).click();
        test.info("Botón de login clickeado");

        wait.until(ExpectedConditions.urlContains("inventory"));
        assertTrue(driver.getCurrentUrl().contains("inventory"), "El login exitoso falló");
        test.pass("Login exitoso: URL contiene 'inventory'");
    }

    @Test
    public void testLoginFallido() {
        test = extentReports.createTest("Login fallido", "Validar login con credenciales incorrectas");

        driver.get("https://www.saucedemo.com/");
        test.info("Navegado a SauceDemo");

        WebDriverWait wait = getWait();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));

        driver.findElement(By.id("user-name")).sendKeys("usuario_invalido");
        test.info("Usuario inválido ingresado");
        driver.findElement(By.id("password")).sendKeys("contraseña_incorrecta");
        test.info("Contraseña inválida ingresada");
        driver.findElement(By.id("login-button")).click();
        test.info("Botón de login clickeado");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message-container")));
        assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"), "El login fallido redirigió a otra página");
        test.pass("Login fallido correcto: la URL sigue siendo la página de login");
    }

    @Test
    public void testTiempoExplicito() {
        test = extentReports.createTest("Tiempo explícito", "Validar que la espera explícita funciona en la página de login");

        driver.get("https://www.saucedemo.com/");
        test.info("Navegado a SauceDemo");

        WebDriverWait wait = getWait();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        test.pass("Tiempo explícito aplicado: el botón de login está visible y listo para interactuar");

        assertTrue(driver.findElement(By.id("login-button")).isDisplayed(), "El botón de login no está visible después de la espera explícita");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}