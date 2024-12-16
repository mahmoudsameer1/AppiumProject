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
import java.util.Properties;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class Base {

    public AppiumDriver driver;
    private UiAutomator2Options androidOptions;
    private XCUITestOptions iosOptions;
    private static ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();
    private AppiumDriverLocalService service;
    public Properties prop;

    public static final String BROWSERSTACK_USERNAME = "mahmoudsameer_QCshyF";
    public static final String BROWSERSTACK_ACCESS_KEY = "xscrZZa7CFssqgRQgV16";
    public static final String BROWSERSTACK_URL = "https://" + BROWSERSTACK_USERNAME + ":" + BROWSERSTACK_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    private boolean isLocalRun; // Flag to track if tests are running locally

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters({"useBrowserStack"})
    public void runServer(@Optional("false") boolean useBrowserStack) {
        isLocalRun = !useBrowserStack; // Set the flag based on the parameter
        if (isLocalRun) {
            // Start the Appium server only for local runs
            AppiumServiceBuilder builder = new AppiumServiceBuilder();
            builder.withAppiumJS(new File("C:/Users/skc/AppData/Roaming/npm/node_modules/appium"));
            builder.usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"));
            builder.withIPAddress("127.0.0.1");
            builder.usingPort(4723);

            service = AppiumDriverLocalService.buildService(builder);
            service.start();

            if (service.isRunning()) {
                System.out.println("Appium server started successfully!");
            } else {
                throw new RuntimeException("Failed to start the Appium server.");
            }
        }
    }

    @BeforeClass(alwaysRun = true)
    @Parameters({"platformName", "useBrowserStack"})
    public void runApplication(String platformName, boolean useBrowserStack) throws IOException {
        FileReader file = new FileReader("./src/test/resources/config.properties");
        prop = new Properties();
        prop.load(file);

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
                androidOptions.setPlatformVersion(prop.getProperty("pltafromVersion"));
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

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            DRIVER.remove();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void stopServer() {
        if (isLocalRun && service != null && service.isRunning()) {
            service.stop();
            System.out.println("Appium server stopped successfully!");
        }
    }
}