package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserStackConfig {
    private static final String USERNAME = "ishachaudhary_3QURqs";
    private static final String ACCESS_KEY = "UqdhjsfZKaQkwGcwR1MZ";
    private static final String BROWSERSTACK_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    // Toggle between LOCAL and BROWSERSTACK execution
    private static final boolean USE_BROWSERSTACK = false;

    public static WebDriver getWebDriver(String browser, String os, String device) throws MalformedURLException {
        if (USE_BROWSERSTACK) {
            return new RemoteWebDriver(new URL(BROWSERSTACK_URL), getCapabilities(browser, os, device));
        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu");
            return new ChromeDriver(options);
        }
    }

    public static DesiredCapabilities getCapabilities(String browser, String os, String device) {
        DesiredCapabilities caps = new DesiredCapabilities();
        if (device.isEmpty()) {
            caps.setCapability("browserName", browser);
            caps.setCapability("os", os);
            caps.setCapability("os_version", "latest");
        } else {
            caps.setCapability("browserName", "Chrome");
            caps.setCapability("device", device);
            caps.setCapability("realMobile", "true");
        }
        caps.setCapability("browserstack.selenium_version", "4.8.0");
        return caps;
    }
}
