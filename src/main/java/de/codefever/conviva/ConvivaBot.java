package de.codefever.conviva;

import de.codefever.conviva.api.whatsapp.WhatsAppUiBot;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import org.openqa.selenium.chrome.ChromeOptions;

public class ConvivaBot implements Loggable, PropertyManagerProvider, WebDriverManagerProvider {

    public static void main(String[] args) {

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless, new ChromeConfig() {
            @Override
            public void configure(ChromeOptions options) {
                options.addArguments("--disable-dev-shm-usage");
                // https://github.com/puppeteer/puppeteer/issues/1914
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.91 Safari/537.36");
            }
        });

        PROPERTY_MANAGER.loadProperties("conviva.properties");
        final WhatsAppUiBot whatsAppUiBot = new WhatsAppUiBot();
        whatsAppUiBot.run();

        // final exit
        WEB_DRIVER_MANAGER.requestShutdownAllSessions();
        System.exit(0);
    }
}
