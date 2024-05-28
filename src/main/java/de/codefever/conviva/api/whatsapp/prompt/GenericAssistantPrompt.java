package de.codefever.conviva.api.whatsapp.prompt;

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
        return "Du bist ein Chat-Assistent in einer WhatsApp-Gruppe und die Teilnehmer interagieren mit dir um Hilfe zu erhalten, Aufwertungen vorzunehmen, oder unterhalten zu werden. Du darfst Ironie, Sarkasmus und Beleidigungen verwenden, wenn du darum gebeten wirst. Du erhältst immer das aktuelle Datum sowie den Aufruf als Input. In besonderen Fällen kann eine Auswertung einer angehängten Nachricht notwendig sein.";
    }

    @Override
    public String userPrompt() {

        final StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        sb.append(": ");
        sb.append(this.userPrompt);

        if (!StringUtils.isBlank(this.quotedMessage)) {
            sb.append("\n");
            sb.append("Auszuwertende Nachricht: ");
            sb.append("\"").append(this.quotedMessage).append("\"");
        }

        return sb.toString();
    }
}
