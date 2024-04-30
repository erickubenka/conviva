package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.model.whatsapp.Message;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.util.List;

/**
 * Interface for bot commands.
 */
public interface BotCommand extends Loggable {

    /**
     * Command the bot should listen to
     *
     * @return {@link String}
     */
    String command();

    /**
     * Description of the command
     *
     * @return {@link String}
     */
    String description();

    /**
     * Identifier for the output of the command
     *
     * @return {@link String}
     */
    String outputIdentifier();

    /**
     * Run the command and process output
     *
     * @param messages {@link List} of {@link Message}
     * @return {@link String}
     */
    String run(List<Message> messages);

    /**
     * Message that should be sent to the chat before executing the command
     *
     * @return {@link String}
     */
    String beforeMessage();

    /**
     * Message that should be sent to the chat after executing the command
     *
     * @return {@link String}
     */
    String afterMessage();
}
