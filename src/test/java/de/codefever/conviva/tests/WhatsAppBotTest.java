package de.codefever.conviva.tests;

import de.codefever.conviva.AbstractTest;
import de.codefever.conviva.api.general.prompt.GenericAssistantPrompt;
import de.codefever.conviva.api.general.prompt.SummaryPrompt;
import de.codefever.conviva.api.openai.CompletionsApiClient;
import de.codefever.conviva.api.openai.ResponsesApiClient;
import de.codefever.conviva.api.whatsapp.WhatsAppUiBot;
import de.codefever.conviva.model.openai.Prompt;
import de.codefever.conviva.model.whatsapp.Message;
import de.codefever.conviva.page.whatsapp.ChatPage;
import de.codefever.conviva.page.whatsapp.ConnectWithNumberPage;
import de.codefever.conviva.page.whatsapp.HomePage;
import de.codefever.conviva.page.whatsapp.LoginPage;
import de.codefever.conviva.page.whatsapp.PhoneNumberVerificationPage;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WhatsAppBotTest extends AbstractTest implements PageFactoryProvider {

    @Test
    public void testT00_OpenWhatsApp() {
        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        Screenshot screenshot = UITestUtils.takeScreenshot(page.getWebDriver(), true);
        File screenshotFile = screenshot.getScreenshotFile();
        log().info("Screenshot: " + screenshotFile.getAbsolutePath());

    }

    @Test
    public void testT00_InvestigateNumber() {

        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        ConnectWithNumberPage connectWithNumberPage = page.goToConnectWithNumberPage();
        connectWithNumberPage = connectWithNumberPage.selectCountry("Deutschland");

        PhoneNumberVerificationPage phoneNumberVerificationPage = connectWithNumberPage.connectWithNumber("1628293597");

        String s = phoneNumberVerificationPage.readVerificationCode();
        log().info("Verification Code: " + s);
        HomePage homePage = phoneNumberVerificationPage.waitForNumberVerified();
        UITestUtils.takeScreenshot(homePage.getWebDriver(), true);

        ChatPage chatPage = homePage.openChat("AktiF_gruppe Sauerland");
        UITestUtils.takeScreenshot(homePage.getWebDriver(), true);
        List<Message> messages = chatPage.visibleMessages(5, false);

        ASSERT.assertNotEquals(messages.size(), 0);
    }

    @Test
    public void testT01_InitHistory() {

        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        HomePage homePage = page.waitForQrCodeScanned();

        ChatPage chatPage = homePage.openChat("Eric Kubenka");
        List<Message> messages = chatPage.allMessagesAfter(LocalDateTime.now().minusHours(2), 2, true);
        ASSERT.assertNotEquals(messages.size(), 0);
    }

    @Test
    public void testT02_SendMultilineMessage() {
        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        HomePage homePage = page.waitForQrCodeScanned();
        ChatPage chatPage = homePage.openChat("Eric Kubenka");
        chatPage.sendMessage("Hello\nHow are you?\nIm fine, thank you.\nWhat are you doing?\nI am writing a test.\nOh, cool.\nYes, it is.\nBye.\nBye.");
    }

    @Test
    public void testT02_ChatGPT_CompletionsApi() {

        final String sayThisIsATest = new CompletionsApiClient().postCompletion(new Prompt() {
            @Override
            public String systemPrompt() {
                return "You are a echo bot, and repeat everything you received from me.";
            }

            @Override
            public String userPrompt() {
                return "Say this is a test";
            }
        });

        log().info("GPT: " + sayThisIsATest);
    }

    @Test
    public void testT03_RunSummaryForTestData() {
        // random messages for testing
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("[07:49, 4/19/2024] User:", "Unglaublich, dass Dortmund gegen Bayern gewinnen wird!"));
        messages.add(new Message("[07:50, 4/19/2024] OtherUser:", "Ja, das war wirklich eine Überaschung."));
        messages.add(new Message("[07:54, 4/19/2024] OtherUser:", "Vor allem, dass es dann ausgerechnet Hummels ist."));
        messages.add(new Message("[07:54, 4/19/2024] OtherUser:", "Abseits?"));
        messages.add(new Message("[07:54, 4/19/2024] User:", "Sah für mich auch danach aus."));
        messages.add(new Message("[08:13, 4/19/2024] User:", "Das ist doch kein Elfmeter"));
        messages.add(new Message("[08:14, 4/19/2024] OtherUser:", "Und zack, sind die Bayern wieder im Spiel."));

        messages.add(new Message("[12:15, 4/19/2024] User:", "Meien Aktien stehen heut überhaupt nicht gut dar"));
        messages.add(new Message("[12:16, 4/19/2024] OtherUser:", "Ja, das ist wirklich schade. Was hast du denn im Portfolie, das dich so drückt?"));
        messages.add(new Message("[12:17, 4/19/2024] User:", "Ich habe ein paar Aktien von VW und BMW"));
        messages.add(new Message("[12:18, 4/19/2024] OtherUser:", "Das ist natürlich nicht so gut. Aber das wird schon wieder. Die Wirtschaft erholt sich ja langsam wieder."));
        messages.add(new Message("[12:19, 4/19/2024] User:", "Ja, das hoffe ich auch."));
        messages.add(new Message("[12:20, 4/19/2024] User:", "Da msus eben mein nVidia Kram alles richten"));
        messages.add(new Message("[12:21, 4/19/2024] OtherUser:", "Ja, das ist eine gute Idee. Die sind ja auch immer sehr stabil."));
        messages.add(new Message("[12:21, 4/19/2024] OtherUser:", "Ich bau eher auf ETF und da auch nur wenige."));
        messages.add(new Message("[12:22, 4/19/2024] User:", "Ja, das ist auch eine gute Idee. Ich bin da eher der Zocker."));

        messages.add(new Message("[17:51, 4/19/2024] User:", "Die neue Folge vom ZDF magazin ist auch sehenswert."));
        messages.add(new Message("[17:52, 4/19/2024] OtherUser:", "Ja, das stimmt."));
        messages.add(new Message("[17:53, 4/19/2024] OtherUser:", "Die Berichte sind immer sehr informativ."));
        messages.add(new Message("[17:54, 4/19/2024] User:", "Ich finde die Moderation auch sehr gut."));
        messages.add(new Message("[17:55, 4/19/2024] OtherUser:", "Und mit dem Thema bzgl. Gotha haben sie natürlich absolut recht."));


        final SummaryPrompt prompt = new SummaryPrompt(messages);
        final String summary = new ResponsesApiClient().postResponseRequest(prompt);
        log().info("Summary: " + summary);
    }

    @Test
    public void testT04_InitHistoryAndRunSummary() {
        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        HomePage homePage = page.waitForQrCodeScanned();
        ChatPage chatPage = homePage.openChat("AktiF_gruppe Sauerland");
        List<Message> messages = chatPage.allMessagesAfter(LocalDateTime.now().minusHours(4), 2, true);
        final SummaryPrompt prompt = new SummaryPrompt(messages);
        final String summary = new ResponsesApiClient().postResponseRequest(prompt);
        log().info("Summary: " + summary);
    }

    @Test
    public void testT05_InitHistoryAndSendSummary() {
        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        HomePage homePage = page.waitForQrCodeScanned();
        ChatPage chatPage = homePage.openChat("AktiF_gruppe Sauerland");

        List<Message> messages = chatPage.allMessagesAfter(LocalDateTime.now().minusHours(4), 2, true);
        final SummaryPrompt prompt = new SummaryPrompt(messages);
        final String summary = new ResponsesApiClient().postResponseRequest(prompt);
        log().info("Summary: " + summary);

        chatPage = chatPage.openChat("Eric Kubenka");
        chatPage = chatPage.sendMessage(summary);
    }

    @Test
    public void testT06_ChatGPT_ResponseApi() {

        final String sayThisIsATest = new ResponsesApiClient().postResponseRequest(new Prompt() {
            @Override
            public String systemPrompt() {
                return "You are a echo bot, and repeat everything you received from me.";
            }

            @Override
            public String userPrompt() {
                return "Say this is a test";
            }
        });

        log().info("GPT: " + sayThisIsATest);
    }

    @Test
    public void testT07_ChatGPT_ResponseApi_Advanced() {

        final String genericPromptResponse = new ResponsesApiClient().postResponseRequest(new GenericAssistantPrompt("Bitte denk ganz genau nach bevor du antwortest. Es mir äußerst wichtig, dass du mir das richtige Datum des folgenden Spiels und das Ergebnis nennen kannst. Bitte gib deine Quelle zu dem Ergebnis an.\n" +
                "Wann fand das letzte Bundesligaspiel zwischen dem FC Bayern München und dem TSV 1860 München statt und wie ging es aus? Wie gesagt, bitte denk ganz genau nach, bevor du antwortest."));

        log().info("GPT: " + genericPromptResponse);
    }

    @Test
    public void testT10_RunBot() {
        LoginPage page = PAGE_FACTORY.createPage(LoginPage.class);
        new WhatsAppUiBot("AktiF_gruppe Sauerland").run();
    }

}
