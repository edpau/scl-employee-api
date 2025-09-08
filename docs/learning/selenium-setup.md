# Selenium Setup -- Employee Project

## 1. Install ChromeDriver (macOS)

```bash
brew install chromedriver
```

- First run may trigger macOS security:\
  **System Settings → Privacy & Security → "chromedriver was blocked"
  → Allow anyway**\
- Then rerun your test once.

------------------------------------------------------------------------

## 2. Create a Dedicated Test Database

Open MySQL and create a throwaway database for tests:

```sql
CREATE DATABASE scl_employee_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

------------------------------------------------------------------------

## 3. Add `application.test.conf` for Play

Minimal config pointing to your test DB:

```hocon
slick.dbs.default.profile = "slick.jdbc.MySQLProfile$"
slick.dbs.default.db.driver = "com.mysql.cj.jdbc.Driver"
slick.dbs.default.db.url = "jdbc:mysql://localhost:3306/scl_employee_test?characterEncoding=UTF-8&useUnicode=true&serverTimezone=UTC"
slick.dbs.default.db.user = "root"

play.evolutions.db.default.autoApply = true
play.evolutions.enabled = true

logger.play=DEBUG
play.modules.enabled += "Module"

play.filters.enabled = [
  "play.filters.cors.CORSFilter",
  "play.filters.headers.SecurityHeadersFilter",
  "play.filters.hosts.AllowedHostsFilter"
]

play.filters.cors {
  allowedOrigins = ["http://localhost:5173"]  # Vite dev server origin
  allowedHttpMethods = ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"]
  allowedHttpHeaders = ["Accept", "Content-Type", "Origin", "Authorization"]
  preflightMaxAge = 1h
  allowCredentials = true
}
```

------------------------------------------------------------------------

## 4. Start Apps in Test Mode

**Backend terminal:**

```bash
sbt -Dconfig.resource=application.test.conf run
```

**Frontend terminal:**

```bash
npm run dev
# typically runs on http://localhost:5173
```

> Keep this manual start for now. Later, you can auto-start Play inside
> tests with `TestServer`.

------------------------------------------------------------------------

## 5. Add Test Dependencies

Update `build.sbt` in the Scala project that hosts the tests:

```scala
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.scalatestplus" %% "selenium-4-21" % "3.2.19.0" % Test
)
```

Reload sbt / reimport in IntelliJ.

------------------------------------------------------------------------

## 6. Stabilise UI Selectors

Add stable attributes to React components to avoid brittle tests:

```jsx
<input data-testid="emp-firstName" ... />
<input data-testid="emp-lastName" ... />
<input data-testid="emp-email" ... />
<button data-testid="emp-submit">Create</button>
```

Then use them in Selenium:

```scala
By.cssSelector("[data-testid='emp-firstName']")
```

(IDs work too if you already have them. `data-testid` is just safer
against semantic changes.)

------------------------------------------------------------------------

## 7. First Smoke Test

Create `src/test/scala/e2e/EndToEnd.scala`:

```scala
package scala.e2e

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
    assert(h1.getText.nonEmpty, "Expected an <h1> to be present and non-empty")
  }
}
```
