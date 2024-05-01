package de.codefever.conviva.page.whatsapp;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PhoneNumberVerificationPage extends Page {

    @Check
    private final UiElement verificationCode = find(By.xpath("//div[@aria-details='link-device-phone-number-code-screen-instructions'] "));

    public PhoneNumberVerificationPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Read the verification code from the page
     *
     * @return {@link HomePage
     */
    public String readVerificationCode() {
        this.verificationCode.expect().displayed().is(true);
        return this.verificationCode.expect().attribute("data-link-code").getActual();
    }

    /**
     * Wait for the number to be verified
     *
     * @return {@link HomePage
     */
    public HomePage waitForNumberVerified() {
        CONTROL.waitFor(300, () -> {
            log().info("Waiting for number verification, current code is:{}", readVerificationCode());
            this.verificationCode.expect().displayed(false);
        });
        return createPage(HomePage.class);
    }
}
