package com.API.pageObjects;

import java.time.Duration;
import java.util.Arrays;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import com.API.base.Base;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;


public class HomePage extends Base{
    
	
    public HomePage(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"Graphics\"]")
    private WebElement access;
    
    @AndroidFindBy(accessibility = "Regions")
    private WebElement Regions;
    
    public void clickaccess () {
    	access.click();
    }
	
    public void swipdown() {
    	
    	final var finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    	var start = new Point(161, 1540);
    	var end = new Point (250, 287);
    	var swipe = new Sequence(finger, 1);
    	swipe.addAction(finger.createPointerMove(Duration.ofMillis(0),
    	    PointerInput.Origin.viewport(), start.getX(), start.getY()));
    	swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    	swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000),
    	    PointerInput.Origin.viewport(), end.getX(), end.getY()));
    	swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    	getDriver().perform(Arrays.asList(swipe));
    }

}
