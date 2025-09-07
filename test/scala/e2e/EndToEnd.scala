package scala.e2e

import io.appium.java_client.functions.ExpectedCondition
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.time.Duration


class EndToEnd extends AnyFunSuite with BeforeAndAfterAll {

  private var driver: WebDriver = _
  private val baseUrl = "http://localhost:5173"
  private val backendUp = "http://localhost:9000"

  override def beforeAll(): Unit = {
    System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver")
    driver = new ChromeDriver()
  }

  override def afterAll(): Unit = {
    if (driver != null) driver.quit()
  }

  test("frontend loads homepage") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val h1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")))
    assert((h1.getText.nonEmpty), "Expected an <h1> to be present and non-empty")
  }
}
