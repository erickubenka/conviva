package de.codefever.conviva.page.whatsapp;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage representation
 */
public class LoginPage extends Page {

    @Check
//    private final UiElement qrCode = find(By.cssSelector("canvas[aria-label='Scan me!']"));
    private final UiElement qrCode = find(By.cssSelector("canvas"));

    //    @Check
//    private UiElement buttonConnectWithNumber = find(By.xpath("//span[@role='button' and text()='Via Telefonnummer verknüpfen']"));
//    private final UiElement buttonConnectWithNumber = find(By.xpath("//span[@role='button' and text()='Link with phone number']"));
//    private final UiElement buttonConnectWithNumber = find(By.xpath("//span[@role='button']"));
    private final UiElement buttonConnectWithNumber = find(By.xpath("//span[@role='button']/*[contains(text(), 'Mit Telefonnummer anmelden') or contains(text(), 'Log in with phone number')]"));

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Advance to the ConnectWithNumberPage
     *
     * @return {@link ConnectWithNumberPage}
     */
    public ConnectWithNumberPage goToConnectWithNumberPage() {
        buttonConnectWithNumber.click();
        return createPage(ConnectWithNumberPage.class);
    }

    /**
     * Get the qrCode element
     *
     * @return {@link UiElement}
     */
    public UiElement getQrCode() {
        return qrCode;
    }

    /**
     * Wait for the qrCode to be scanned
     *
     * @return {@link HomePage}
     */
    public HomePage waitForQrCodeScanned() {
        CONTROL.waitFor(300, () -> {
            this.qrCode.expect().displayed(false);
        });

        return createPage(HomePage.class);
    }
}
