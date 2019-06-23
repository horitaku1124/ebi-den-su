package selenium

import org.junit.Test
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.openqa.selenium.*

import org.openqa.selenium.By.*

import com.github.horitaku1124.selenium_enhance.TestBase.*


@RunWith(value = Parameterized::class)
class SeleniumTest1(private val browser: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Browser = {0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf<Any>("firefox"),
                arrayOf<Any>("chrome"),
                arrayOf<Any>("safari")
            )
        }
    }

    private val OS = System.getProperty("os.name").toLowerCase()
    private val isWindows = OS.contains("win")
    private val BASE_URL = "https://docs.seleniumhq.org"


    private fun css(cssSelector: String): By {
        return cssSelector(cssSelector)
    }


    private lateinit var driver: WebDriver
    private var uploadFilePath: String? = null
    @Before
    fun before() {
        driver = setupDriver(browser, isWindows)
        val path = System.getProperty("user.dir")
        //path = path.replaceAll("\\\\", "/");
        uploadFilePath = "$path/src/test/resources/"

        loginIfNeed(driver, BASE_URL)
    }

    @After
    fun after() {
        driver.quit()
    }



    @Test
    fun test0() {
        var verifyText:String? = "";
        var targets:Array<By>? = null;

        driver.get(BASE_URL + "/")
        driver.manage().window().setSize( Dimension(1098, 807))
        Thread.sleep(100)


        targets = arrayOf(
            css("h2:nth-child(1)"),
            xpath("//div[@id='mainContent']/h2")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)

        targets = arrayOf<By>(
            css("h2:nth-child(1)"),
            xpath("//div[@id='mainContent']/h2")
        )
        verifyText = findAnyElement(driver, targets).text
        assertEquals(verifyText, "What is Selenium?")
        Thread.sleep(100)


        targets = arrayOf(
            css("#menu_projects > a"),
            xpath("//li[@id='menu_projects']/a")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)


        targets = arrayOf(
            css("h2"),
            xpath("//div[@id='mainContent']/h2")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)


        targets = arrayOf(
            css("h2"),
            xpath("//div[@id='mainContent']/h2")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)


        targets = arrayOf(
            id("container"),
            css("#container")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)


        targets = arrayOf(
            css("h2"),
            xpath("//div[@id='mainContent']/h2")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)


        targets = arrayOf(
            css("h2"),
            xpath("//div[@id='mainContent']/h2")
        )

        findAndClick(driver, targets)
        Thread.sleep(100)

        targets = arrayOf<By>(
            css("h2"),
            xpath("//div[@id='mainContent']/h2")
        )
        verifyText = findAnyElement(driver, targets).text
        assertEquals(verifyText, "Selenium Projects")


    }


}
