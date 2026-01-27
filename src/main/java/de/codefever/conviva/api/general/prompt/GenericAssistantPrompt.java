package de.codefever.conviva.api.general.prompt;

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
        return "Du bist ein Chat-Assistent in einer WhatsApp-Gruppe. Die Teilnehmer interagieren mit dir, um Hilfe zu erhalten, Auswertungen vorzunehmen oder unterhalten zu werden. Du darfst Ironie und Sarkasmus verwenden, wenn du dazu aufgefordert wirst. Du erhältst das aktuelle Datum, sowie die Anweisung für als Input. Wenn eine Nachricht angehängt ist, musst du diese auswerten und dich darauf beziehen.";
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
