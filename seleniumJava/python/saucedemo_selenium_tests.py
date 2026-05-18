import os
import html
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

# Si quieres usar ChromeDriver manualmente, pon la ruta aquí:
# CHROMEDRIVER_PATH = "C:/ruta/a/chromedriver.exe"
CHROMEDRIVER_PATH = None

REPORT_DIR = os.path.join(os.path.dirname(__file__), "Reports")
REPORT_FILE = os.path.join(REPORT_DIR, "python-selenium-report.html")
BASE_URL = "https://www.saucedemo.com/"


def create_driver():
    options = webdriver.ChromeOptions()
    options.add_argument("--start-maximized")

    if CHROMEDRIVER_PATH:
        service = Service(CHROMEDRIVER_PATH)
    else:
        try:
            from webdriver_manager.chrome import ChromeDriverManager
        except ImportError as e:
            raise ImportError(
                "No se encontró webdriver-manager. Instálalo con: pip install webdriver-manager"
            ) from e
        service = Service(ChromeDriverManager().install())

    return webdriver.Chrome(service=service, options=options)


def wait_for_element(driver, by, value, timeout=10):
    return WebDriverWait(driver, timeout).until(
        EC.presence_of_element_located((by, value))
    )


def wait_for_clickable(driver, by, value, timeout=10):
    return WebDriverWait(driver, timeout).until(
        EC.element_to_be_clickable((by, value))
    )


def login(driver, username, password):
    driver.get(BASE_URL)
    driver.find_element(By.ID, "user-name").send_keys(username)
    driver.find_element(By.ID, "password").send_keys(password)
    driver.find_element(By.ID, "login-button").click()


def test_invalid_login(driver):
    try:
        login(driver, "usuario_invalido", "contraseña_incorrecta")
        message = wait_for_element(driver, By.CSS_SELECTOR, "div.error-message-container").text
        passed = "Epic sadface" in message or "Username and password do not match" in message
        return {
            "name": "Login inválido",
            "status": "PASS" if passed else "FAIL",
            "details": f"Error mostrado: {message}" if passed else "No se mostró el mensaje de error esperado"
        }
    except TimeoutException:
        return {
            "name": "Login inválido",
            "status": "FAIL",
            "details": "No se encontró el mensaje de error después del login inválido"
        }
    except Exception as e:
        return {
            "name": "Login inválido",
            "status": "FAIL",
            "details": f"Excepción: {e}"
        }


def test_add_product_to_cart(driver):
    try:
        login(driver, "standard_user", "secret_sauce")

        cart_results = []

        wait_for_clickable(driver, By.ID, "add-to-cart-sauce-labs-backpack")
        driver.find_element(By.ID, "add-to-cart-sauce-labs-backpack").click()
        cart_results.append({
            "name": "Agregar producto al carrito",
            "status": "PASS",
            "details": "Se hizo clic en el botón Agregar al carrito"
        })

        wait_for_element(driver, By.CLASS_NAME, "shopping_cart_badge")
        count = driver.find_element(By.CLASS_NAME, "shopping_cart_badge").text
        passed = count == "1"
        cart_results.append({
            "name": "Validar que el carrito tenga 1 producto",
            "status": "PASS" if passed else "FAIL",
            "details": f"El carrito muestra {count} producto(s)"
        })

        driver.find_element(By.CLASS_NAME, "shopping_cart_link").click()
        item_name = wait_for_element(driver, By.CLASS_NAME, "inventory_item_name").text
        item_passed = item_name.strip() == "Sauce Labs Backpack"
        cart_results.append({
            "name": "Validar producto dentro del carrito",
            "status": "PASS" if item_passed else "FAIL",
            "details": f"Producto en carrito: {item_name}"
        })

        return cart_results
    except TimeoutException:
        return [
            {
                "name": "Agregar producto al carrito",
                "status": "FAIL",
                "details": "No se encontró el botón de agregar al carrito"
            },
            {
                "name": "Validar que el carrito tenga 1 producto",
                "status": "FAIL",
                "details": "No se encontró el contador del carrito"
            },
            {
                "name": "Validar producto dentro del carrito",
                "status": "FAIL",
                "details": "No se encontró el producto dentro del carrito"
            }
        ]
    except Exception as e:
        return [
            {
                "name": "Agregar producto al carrito",
                "status": "FAIL",
                "details": f"Excepción: {e}"
            },
            {
                "name": "Validar que el carrito tenga 1 producto",
                "status": "FAIL",
                "details": f"Excepción: {e}"
            }
        ]


def build_report(results):
    if not os.path.exists(REPORT_DIR):
        os.makedirs(REPORT_DIR, exist_ok=True)

    rows = []
    for result in results:
        status_color = "#28a745" if result["status"] == "PASS" else "#dc3545"
        rows.append(
            f"<tr><td>{html.escape(result['name'])}</td><td>{result['status']}</td><td style='color:{status_color};'>{html.escape(result['details'])}</td></tr>"
        )

    html_content = f"""
    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte Selenium Python</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 24px; }}
            table {{ border-collapse: collapse; width: 100%; }}
            th, td {{ border: 1px solid #ccc; padding: 10px; text-align: left; }}
            th {{ background-color: #f0f0f0; }}
        </style>
    </head>
    <body>
        <h1>Reporte de pruebas Selenium Python</h1>
        <table>
            <thead>
                <tr><th>Prueba</th><th>Estado</th><th>Detalle</th></tr>
            </thead>
            <tbody>
                {''.join(rows)}
            </tbody>
        </table>
    </body>
    </html>
    """

    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        f.write(html_content)

    print(f"Reporte generado en: {os.path.abspath(REPORT_FILE)}")


if __name__ == "__main__":
    driver = create_driver()
    results = []
    try:
        results.append(test_invalid_login(driver))
        driver.delete_all_cookies()
        cart_results = test_add_product_to_cart(driver)
        if isinstance(cart_results, list):
            results.extend(cart_results)
        else:
            results.append(cart_results)
    finally:
        driver.quit()
        build_report(results)
