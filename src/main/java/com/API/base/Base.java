package com.API.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.testng.annotations.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import java.nio.file.Paths;


public class Base {

    public AppiumDriver driver;
    private UiAutomator2Options androidOptions;
    private XCUITestOptions iosOptions;
    private static ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();
    private AppiumDriverLocalService service;
    public Properties prop;
    public Logger logger;

    public static final String BROWSERSTACK_USERNAME = "mahmoudsameer_QCshyF";
    public static final String BROWSERSTACK_ACCESS_KEY = "xscrZZa7CFssqgRQgV16";
    public static final String BROWSERSTACK_URL = "https://" + BROWSERSTACK_USERNAME + ":" + BROWSERSTACK_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }
    
    @Parameters({"useBrowserStack"})
    @BeforeTest
    public void runServer(boolean useBrowserStack) {
        if (!useBrowserStack) {
            AppiumServiceBuilder builder = new AppiumServiceBuilder();
            builder.withAppiumJS(new File("C:/Users/skc/AppData/Roaming/npm/node_modules/appium"));
            builder.usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"));
            builder.withIPAddress("127.0.0.1");
            builder.usingPort(4723);

            // Add the gestures plugin argument
            builder.withArgument(() -> "--use-plugins", "gestures");

            // Build and start the service
            service = AppiumDriverLocalService.buildService(builder);
            service.start();
        }
    }	

    @BeforeClass
    @Parameters({"platformName", "useBrowserStack"})
    public void runApplication(String platformName, boolean useBrowserStack) throws IOException {
    	
        FileReader file = new FileReader("./src/test/resources/config.properties");
        prop = new Properties();
        prop.load(file);
        logger = LogManager.getLogger(this.getClass());

        if (useBrowserStack) {
            // BrowserStack Configuration
            if (platformName.equalsIgnoreCase("Android")) {
                androidOptions = new UiAutomator2Options();
                androidOptions.setCapability("appium:deviceName", prop.getProperty("bsDeviceNameAndroid"));
                androidOptions.setCapability("appium:platformVersion", prop.getProperty("bsPlatformVersionAndroid"));
                androidOptions.setCapability("platformName", prop.getProperty("bsPlatformNameAndroid"));
                androidOptions.setCapability("appium:app", prop.getProperty("bsAppAndroid"));
                androidOptions.setCapability("appium:browserstackLocal", false);
                androidOptions.setCapability("appium:automationName", prop.getProperty("automationName"));
                driver = new AndroidDriver(new URL(BROWSERSTACK_URL), androidOptions);
            } else if (platformName.equalsIgnoreCase("iOS")) {
                iosOptions = new XCUITestOptions();
                iosOptions.setCapability("deviceName", prop.getProperty("bsDeviceNameIOS"));
                iosOptions.setCapability("platformVersion", prop.getProperty("bsPlatformVersionIOS"));
                iosOptions.setCapability("app", prop.getProperty("bsAppIOS"));
                iosOptions.setCapability("project", "Appium Project");
                iosOptions.setCapability("build", "Build 1");
                iosOptions.setCapability("name", "iOS Test");
                driver = new IOSDriver(new URL(BROWSERSTACK_URL), iosOptions);
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platformName);
            }
        } else {
            // Local Configuration
            if (platformName.equalsIgnoreCase("Android")) {
                androidOptions = new UiAutomator2Options();
                androidOptions.setDeviceName(prop.getProperty("deviceName"));
                androidOptions.setPlatformName(platformName);
                androidOptions.setAutomationName(prop.getProperty("automationName"));
                androidOptions.setPlatformVersion(prop.getProperty("platformVersion"));
                androidOptions.setApp(System.getProperty("user.dir") + "/src/test/resources/ApiDemos-debug.apk");
                driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), androidOptions);
            } else if (platformName.equalsIgnoreCase("iOS")) {
                iosOptions = new XCUITestOptions();
                iosOptions.setDeviceName(prop.getProperty("deviceName"));
                iosOptions.setPlatformName(platformName);
                iosOptions.setAutomationName(prop.getProperty("iosAutomationName"));
                iosOptions.setBundleId(prop.getProperty("iosBundleId")); // iOS-specific capability
                iosOptions.setApp(System.getProperty("user.dir") + "/src/test/resources/app/iOS-SauceDemoApp.ipa");
                driver = new IOSDriver(new URL("http://0.0.0.0:4723"), iosOptions);
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platformName);
            }
        }

        DRIVER.set(driver);
    }
    
    public String takeScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String screenshotPath = Paths
                .get(System.getProperty("user.dir"), "allure-results/screenshots", testName + "_" + timestamp + ".png")
                .toString();
        try {
            TakesScreenshot ts = (TakesScreenshot) getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotPath);
            FileHandler.copy(source, destination);
            logger.info("Screenshot captured: " + screenshotPath);
        } catch (IOException e) {
            logger.error("Failed to capture screenshot", e);
        }
        return screenshotPath;
    }

    @AfterClass
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            DRIVER.remove();
        }
    }

    @AfterTest
    public void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }
}