package scala.e2e

import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.time.Duration


class EndToEnd extends AnyFunSuite with BeforeAndAfterAll {

  private var driver: WebDriver = _
  private val baseUrl = "http://localhost:5173"
  private val backendUp = "http://localhost:9000"

  // ------ helpers ------
  private def byTestId(id: String): By =
    By.cssSelector(s"""[data-testid="$id"]""")

  private def waitFor(by: By, seconds: Long = 5) = {
    val w = new WebDriverWait(driver, Duration.ofSeconds(seconds))
    w.until(ExpectedConditions.presenceOfElementLocated(by))
  }

  private def waitClickable(by: By, seconds: Long = 5) = {
    val w = new WebDriverWait(driver, Duration.ofSeconds(seconds))
    w.until(ExpectedConditions.elementToBeClickable(by))
  }

  private def waitGone(by: By, seconds: Long = 5): Boolean = {
    val w = new WebDriverWait(driver, Duration.ofSeconds(seconds))
    w.until(ExpectedConditions.invisibilityOfElementLocated(by))
  }

  override def beforeAll(): Unit = {
    val headless = sys.props.get("headless").contains("true")
    val opts = new ChromeOptions()
    if (headless) opts.addArguments("--headless=new")
    opts.addArguments("--window-size=1280,900")
    driver = new ChromeDriver(opts)
  }

  override def afterAll(): Unit = {
    //    if (driver != null) driver.quit()
    Option(driver).foreach(_.quit())
  }

  // -------- tests ---------

  // Keep for learning purpose, not using helper
  test("frontend loads homepage") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val h1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")))
    assert((h1.getText.nonEmpty), "Expected an <h1> to be present and non-empty")
  }

  test("App Shell: title renders") {
    driver.get(baseUrl)
    val title = waitFor(byTestId("app-title"))
    assert(title.getText.trim == "Employee")
  }

  test("Open Add Employee modal from landing page") {
    driver.get(baseUrl)

    val addBtn = waitClickable(byTestId("add-employee-btn"))
    addBtn.click()

    val modal = waitFor(byTestId("add-employee-modal"))
    val dialog = waitFor(byTestId("add-employee-dialog"))
    val form = waitFor(byTestId("add-employee-form"))

    assert(modal.isDisplayed && dialog.isDisplayed && form.isDisplayed)
  }

  test("Cancel closes Add Employee modal") {
    driver.get(baseUrl)

    waitClickable(byTestId("add-employee-btn")).click()
    val dialog = waitFor(byTestId("add-employee-dialog"))
    assert(dialog.isDisplayed, "Dialog should appear after clicking Add Employee")

    waitClickable(byTestId("add-employee-cancel-btn")).click()
    waitGone(byTestId("add-employee-dialog"))
  }

}
