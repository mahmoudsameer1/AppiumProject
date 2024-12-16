package com.API.pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import com.API.base.Base;

public class HomePage extends Base{
    
    public HomePage(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }
    
    @FindBy(xpath = "//android.widget.TextView[@content-desc=\"Graphics\"]")
    private WebElement access;
    
    public void clickaccess () {
    	access.click();
    }
	
}
