package de.codefever.conviva.api.common.prompt;

import de.codefever.conviva.model.openai.Prompt;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenericAssistantPrompt implements Prompt {

    private final String userPrompt;
    private final String quotedMessage;

    public GenericAssistantPrompt(final String userPrompt, final String quotedMessage) {
        this.userPrompt = userPrompt;
        this.quotedMessage = quotedMessage;
    }

    public GenericAssistantPrompt(final String userPrompt) {
        this.userPrompt = userPrompt;
        this.quotedMessage = null;
    }

    @Override
    public String systemPrompt() {
        return "Du bist Chat-Assistent in einer Chat-Gruppe(Whatsapp, Signal, Telegram, etc.). Die Gruppenmitglieder nutzen dich für Unterstützung, Auswertungen und Unterhaltung. Verwende Ironie oder Sarkasmus nur, wenn ausdrücklich darum gebeten wird. Du erhältst das aktuelle Datum und die jeweilige Anweisung als Input. Wenn eine Nachricht einen Anhang hat, werte ihn aus und beziehe dich konkret darauf.";
    }

    @Override
    public String userPrompt() {

        final StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        sb.append(": ");
        sb.append(this.userPrompt);

        if (!StringUtils.isBlank(this.quotedMessage)) {
            sb.append("\n");
            sb.append("Angehängte Nachricht: ");
            sb.append("\"").append(this.quotedMessage).append("\"");
        }

        return sb.toString();
    }
}
