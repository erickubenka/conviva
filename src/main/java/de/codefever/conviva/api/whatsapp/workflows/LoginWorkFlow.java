package de.codefever.conviva.api.whatsapp.workflows;

import de.codefever.conviva.page.whatsapp.ChatPage;
import de.codefever.conviva.page.whatsapp.ConnectWithNumberPage;
import de.codefever.conviva.page.whatsapp.HomePage;
import de.codefever.conviva.page.whatsapp.LoginPage;
import de.codefever.conviva.page.whatsapp.PhoneNumberVerificationPage;

public class LoginWorkFlow implements WorkFlow<ChatPage> {

    private final String chatName;
    private final String webDriverUUID;

    public LoginWorkFlow(final String chatName, final String webDriverUUID) {
        this.chatName = chatName;
        this.webDriverUUID = webDriverUUID;
    }

    /**
     * Perform the login process based on specified options.
     *
     * @return {@link ChatPage} instance of the chat the bot joined.
     */
    public ChatPage run() {

        if (PROPERTY_MANAGER.getBooleanProperty("conviva.chrome.userdata.persistent.enable")) {
            log().info("Using persistent user data directory: {}. Try to instantiate HomePage instead of LoginPage", PROPERTY_MANAGER.getProperty("conviva.chrome.userdata.persistent.path"));
            try {
                final HomePage homePage = PAGE_FACTORY.createPage(HomePage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
                return homePage.openChat(this.chatName);
            } catch (Exception e) {
                log().error("Error while trying to instantiate HomePage: {}. Will go for login instead.", e.getMessage());
            }
        }

        final LoginPage loginPage = PAGE_FACTORY.createPage(LoginPage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
        if (PROPERTY_MANAGER.getProperty("conviva.auth.mode").equals("phone")) {
            ConnectWithNumberPage connectWithNumberPage = loginPage.goToConnectWithNumberPage();
            connectWithNumberPage = connectWithNumberPage.selectCountry(PROPERTY_MANAGER.getProperty("conviva.auth.phone.country"));
            PhoneNumberVerificationPage phoneNumberVerificationPage = connectWithNumberPage.connectWithNumber(PROPERTY_MANAGER.getProperty("conviva.auth.phone.number"));
            final HomePage homePage = phoneNumberVerificationPage.waitForNumberVerified();
            return homePage.openChat(this.chatName);
        } else {
            final HomePage homePage = loginPage.waitForQrCodeScanned();
            return homePage.openChat(this.chatName);
        }
    }

}
