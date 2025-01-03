package de.codefever.conviva.page.whatsapp;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Represents the page where user can connect web.whatsapp.com with a phone number.
 */
public class ConnectWithNumberPage extends Page {

    @Check
    private final UiElement inputPhoneNumber = find(By.xpath("//input[@aria-label='Type your phone number.' or @aria-label='Gib deine Telefonnummer ein.']"));

    @Check
    private final UiElement buttonNext = find(By.xpath("//button//div[text()='Next' or text()='Weiter']"));

    @Check
    private final UiElement selectCountryButton = find(By.xpath("//button//span[@data-icon='chevron']"));

    public ConnectWithNumberPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Connects with the given phone number.
     *
     * @param phoneNumber {@link String}
     * @return {@link PhoneNumberVerificationPage}
     */
    public PhoneNumberVerificationPage connectWithNumber(final String phoneNumber) {
        inputPhoneNumber.sendKeys(phoneNumber);
        buttonNext.click();
        return createPage(PhoneNumberVerificationPage.class);
    }

    /**
     * Selects the given country.
     *
     * @param country {@link String}
     * @return {@link ConnectWithNumberPage}
     */
    public ConnectWithNumberPage selectCountry(final String country) {
        selectCountryButton.click();

        final UiElement searchCountryInput = find(By.xpath("//span[@data-icon='search']/../..//div[@role='textbox']"));
        searchCountryInput.expect().present().is(true);
        searchCountryInput.sendKeys(country);

        final UiElement buttonListItemCountry = find(By.xpath("//button[@role='listitem']//div[text()='" + country + "']"));
        buttonListItemCountry.expect().displayed().is(true);
        buttonListItemCountry.click();

        return createPage(ConnectWithNumberPage.class);
    }
}
