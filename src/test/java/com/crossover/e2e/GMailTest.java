package com.crossover.e2e;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GMailTest extends TestCase {
	private WebDriver driver;
	private Properties properties = new Properties();

	public void setUp() throws Exception {

		properties.load(new FileReader(new File("src/test/resources/test.properties")));
		System.setProperty("webdriver.chrome.driver", properties.getProperty("webdriver.chrome.driver"));
		driver = new ChromeDriver();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void testSendEmail() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 45);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		long timeStamp = System.currentTimeMillis();
		
		driver.get("https://mail.google.com/");

		WebElement userElement = driver.findElement(By.id("identifierId"));
		userElement.sendKeys(properties.getProperty("username"));

		driver.findElement(By.id("identifierNext")).click();

		Thread.sleep(1000);

		WebElement passwordElement = driver.findElement(By.name("password"));
		passwordElement.sendKeys(properties.getProperty("password"));
		driver.findElement(By.id("passwordNext")).click();

		Thread.sleep(1000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and (.)='Compose']")));
		WebElement composeElement = driver.findElement(By.xpath("//*[@role='button' and (.)='Compose']"));
		composeElement.click();

		Thread.sleep(1000);

		wait.until(ExpectedConditions.elementToBeClickable(By.name("to")));
		driver.findElement(By.name("to")).clear();
		driver.findElement(By.name("to")).sendKeys(String.format("%s@gmail.com", properties.getProperty("username")));

		// set subject for mail
		driver.findElement(By.name("subjectbox")).clear();
		driver.findElement(By.name("subjectbox")).sendKeys(properties.getProperty("email.subject"));

		// enter the message in the body
		WebElement body = driver.findElement(By.cssSelector(".Ar.Au div"));
		body.click();
		if (body.isEnabled() && body.isDisplayed()) {
			body.sendKeys(properties.getProperty("email.body")+" Time sent :"+timeStamp);
		}

		// Label email as "Social"
		List<WebElement> x = driver.findElements(By.xpath("//div[@class='J-J5-Ji J-JN-M-I-JG']"));
		for(WebElement s : x){
			if(s.isDisplayed() && s.isEnabled()){
				s.click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='J-N-Jz' and text()='Label']"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='J-LC-Jz' and text()='Social']/div"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='J-JK-Jz' and text()='Apply']"))).click();
			}
		}

		// click in send button
		driver.findElement(By.xpath("//*[@role='button' and text()='Send']")).click();
		
		// Wait for the email to arrive in the Inbox
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@class='bAq' and text()='Message sent.']")));
		
		// Click on Social tab
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='aKz' and text()='Social']"))).click();
		
		// Mark email as starred
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Time sent :"+timeStamp+"')]/ancestor::tr/td[@class='apU xY']/span"))).click();
		
		// Open the received email
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Time sent :"+timeStamp+"')]"))).click();
		
		// Verify email came under proper Label i.e. "Social"
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@name='^smartlabel_social']")));
		
		// Verify the subject of the received email
		Assert.assertEquals(properties.getProperty("email.subject"), driver.findElement(By.xpath("//h2[@class='hP']")).getText());
		
		// Verify the subject of the received email
		Assert.assertEquals(properties.getProperty("email.body")+" Time sent :"+timeStamp, driver.findElement(By.xpath("//div[@class='a3s aXjCH ']/div[@dir='ltr']")).getText());

		
	}
	
	@Test
	public void afterTest(){
		
	}
	
}
