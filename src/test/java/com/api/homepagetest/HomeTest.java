package com.api.homepagetest;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.API.base.Base;
import com.API.pageObjects.HomePage;

public class HomeTest extends Base{

	private static HomePage homepage;
	
	@Test(priority=1,description="Add a new product and verify it’s added successfully")
	public void createNewProduct() throws InterruptedException{
	    homepage = new HomePage();
	    homepage.clickaccess();
	    Thread.sleep(5000);
	    homepage.swipdown();
	    Thread.sleep(5000);
	}
}
