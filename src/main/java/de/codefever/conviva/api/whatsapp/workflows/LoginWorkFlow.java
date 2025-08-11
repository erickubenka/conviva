package de.codefever.conviva.api.whatsapp.workflows;

import de.codefever.conviva.page.whatsapp.ChatPage;
import de.codefever.conviva.page.whatsapp.ConnectWithNumberPage;
import de.codefever.conviva.page.whatsapp.HomePage;
import de.codefever.conviva.page.whatsapp.LoginPage;
import de.codefever.conviva.page.whatsapp.ModalOverlayPage;
import de.codefever.conviva.page.whatsapp.PhoneNumberVerificationPage;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;

import java.io.File;

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
                handlePossibleModalOverlay();
                final HomePage homePage = PAGE_FACTORY.createPage(HomePage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
                final ChatPage chatPage = homePage.openChat(this.chatName);
                log().info("Screenshot successful login with persistent session.");
                UITestUtils.takeWebDriverScreenshotToFile(WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID), new File("/tmp/img/conviva_latest_login_successful.png"));
                return chatPage;
            } catch (Exception e) {
                log().info("Take Login Error Screenshot!");
                UITestUtils.takeWebDriverScreenshotToFile(WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID), new File("/tmp/img/conviva_latest_login_error.png"));
                log().error("Error while trying to instantiate HomePage: {}. Will go for login instead.", e.getMessage(), e);
            }
        }

        final LoginPage loginPage = PAGE_FACTORY.createPage(LoginPage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
        if (PROPERTY_MANAGER.getProperty("conviva.auth.mode").equals("phone")) {
            ConnectWithNumberPage connectWithNumberPage = loginPage.goToConnectWithNumberPage();
            connectWithNumberPage = connectWithNumberPage.selectCountry(PROPERTY_MANAGER.getProperty("conviva.auth.phone.country"));
            PhoneNumberVerificationPage phoneNumberVerificationPage = connectWithNumberPage.connectWithNumber(PROPERTY_MANAGER.getProperty("conviva.auth.phone.number"));
            final HomePage homePage = phoneNumberVerificationPage.waitForNumberVerified();
            handlePossibleModalOverlay();
            final ChatPage chatPage = homePage.openChat(this.chatName);
            log().info("Screenshot successful login with phone number verification session.");
            UITestUtils.takeWebDriverScreenshotToFile(WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID), new File("/tmp/img/conviva_latest_login_successful.png"));
            return chatPage;
        } else {
            final HomePage homePage = loginPage.waitForQrCodeScanned();
            handlePossibleModalOverlay();
            final ChatPage chatPage = homePage.openChat(this.chatName);
            log().info("Screenshot successful login with QR code scanned");
            UITestUtils.takeWebDriverScreenshotToFile(WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID), new File("/tmp/img/conviva_latest_login_successful.png"));
            return chatPage;
        }
    }

    private void handlePossibleModalOverlay() {
        final ModalOverlayPage modalOverlayPage = PAGE_FACTORY.createPage(ModalOverlayPage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
        log().info("ModalOverlayPage displayed: {}", modalOverlayPage.isModalOverlayDisplayed());

        if (modalOverlayPage.isModalOverlayDisplayed()) {
            modalOverlayPage.closeModalOverlay(HomePage.class);
        } else {
            log().info("Modal overlay is not displayed, continuing.");
        }
    }

}
