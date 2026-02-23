package de.codefever.conviva.api.common.command;

import de.codefever.conviva.model.general.Message;
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
    String run(final Message callToCommand, final List<Message> messages);

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

    /**
     * Determines if this command is available in !help
     *
     * @return {@link boolean}
     */
    boolean isPublic();

    /**
     * Determines if this command should be executed in a separate thread
     *
     * @return {@link boolean}
     */
    boolean isRunInThread();

    /**
     * Determines if the list of {@link Message} is intended for a quoted message command,
     * meaning that only 1 message is in this list and the command was called as a reply to a message which means
     * that the message itself will equal the command, but the quoted message will be present and should be sued for the command itself
     * <p>
     * Returns true when list size is 1 and the message has a quoted message
     * Returns false when more than one message is in the list
     *
     * @param callToCommand {@link List} of {@link Message}
     * @return {@link boolean}
     */
    default boolean isIntendedForQuotedMessage(final Message callToCommand) {
        return callToCommand.hasQuotedMessage();
    }
}
