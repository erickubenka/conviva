package de.codefever.conviva.page.whatsapp;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the home page
 */
public class HomePage extends Page {

//    @Check
//    private UiElement buttonArchived = find(By.xpath("//button[contains(@aria-label,'Archiviert')]"));
//    private UiElement buttonArchived = find(By.xpath("//button[contains(@aria-label,'Archiviert')]"));

    @Check(timeout = 60)
    private UiElement chatList = find(By.xpath("//div[@aria-label='Chatliste']"));
//    private UiElement chatList = find(By.xpath("//div[@aria-label='Chats']"));

    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Opens a chat by title
     *
     * @param title {@link String}
     * @return {@link ChatPage}
     */
    public ChatPage openChat(String title) {
        chatList.find(By.xpath(".//span[@title='" + title + "']")).click();
        return createPage(ChatPage.class);
    }
}
