package de.codefever.conviva.page.whatsapp;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ModalOverlayPage extends Page {

    private final UiElement modalOverlay = find(By.xpath("//div[@data-animate-modal-body]"));

    public ModalOverlayPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Checks if the modal overlay is displayed.
     *
     * @return {@link Boolean} true if the modal overlay is displayed, false otherwise.
     */
    public boolean isModalOverlayDisplayed() {
        return modalOverlay.expect().present().is(true);
    }

    /**
     * Checks if the modal overlay is not displayed.
     *
     * @return {@link Boolean} true if the modal overlay is not displayed, false otherwise.
     */
    public boolean isModalOverlayNotDisplayed() {
        return modalOverlay.expect().present().is(false);
    }

    public <T extends Page> void closeModalOverlay(final Class<T> pageClass) {

        final UiElement closeButton = modalOverlay.find(By.xpath(".//button"));
        closeButton.expect().present().is(true);
        closeButton.click();
        createPage(pageClass);
    }
}
