package com.API.Utilities;

import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import com.API.base.Base;

public class Listener implements ITestListener {

	@Override
	public void onStart(ITestContext context) {
		System.out.println("Test execution is started.......");
	}

	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("Test started...");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("Test passed...");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("Test failed...");

		// Capture a screenshot on failure
		Object currentClass = result.getInstance();
		Base base = (Base) currentClass;

		String screenshotPath = base.takeScreenshot(result.getName());
		attachScreenshotToAllure(screenshotPath);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("Test skipped...");
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("Test execution is completed...");
	}

	private void attachScreenshotToAllure(String screenshotPath) {
		try {
			Path content = Paths.get(screenshotPath);
			Allure.addAttachment("Screenshot", Files.newInputStream(content));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
