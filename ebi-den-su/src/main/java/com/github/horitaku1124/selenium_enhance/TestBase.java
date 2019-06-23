package com.github.horitaku1124.selenium_enhance;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.File;

public class TestBase {
    public static WebDriver setupDriver(String browser, boolean isWindows) {
        WebDriver driver;
        if ("firefox".equals(browser)) {
            System.setProperty("webdriver.gecko.driver", "./drivers/geckodriver" + (isWindows ? ".exe" : ""));
            driver = new FirefoxDriver();
        } else if ("chrome".equals(browser)) {
            System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver" + (isWindows ? ".exe" : ""));
            driver = new ChromeDriver();
        } else if ("safari".equals(browser)) {
            System.setProperty("webdriver.safari.noinstall", "true");
            driver = new SafariDriver();
        } else if ("edge".equals(browser)) {
            System.setProperty("webdriver.edge.driver", "./drivers/MicrosoftWebDriver" + (isWindows ? ".exe" : ""));
            driver = new EdgeDriver();
        } else if ("ie".equals(browser)) {
            System.setProperty("webdriver.ie.driver", "./drivers/IEDriverServer.exe");
            driver = new InternetExplorerDriver();
        } else {
            throw new RuntimeException(browser);
        }
        return driver;
    }

    public static WebElement findAnyElement(WebDriver driver, By[] targets) {
        for(By target: targets) {
            try {
                WebElement element = driver.findElement(target);
                if (element != null) {
                    return element;
                }
            } catch (InvalidArgumentException | NoSuchElementException e) {
                System.err.println(e.getClass().getName() + ":" + target);
                e.printStackTrace();
            }
        }
        throw new RuntimeException("no elements");
    }
    public static void findAndClick(WebDriver driver, By[] targets) {
        for(By target: targets) {
            try {
                WebElement element = driver.findElement(target);
                element.click();
                return;
            } catch (InvalidArgumentException | NoSuchElementException | ElementClickInterceptedException e) {
                System.err.println(e.getClass().getName() + ":" + target);
                e.printStackTrace();
            }
        }
        //throw new RuntimeException("no elements");
    }

    public static void takeScreenShot(WebDriver driver, String dist) {
        File ss = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        boolean result = ss.renameTo(new File(dist));
        if (!result) {
            throw new RuntimeException("failed to save to " + dist + " from " + ss);
        }
    }
    public static void loginIfNeed(WebDriver driver, String baseUrl) {

    }
}
