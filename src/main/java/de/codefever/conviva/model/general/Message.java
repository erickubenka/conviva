package de.codefever.conviva.model.general;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.time.LocalDateTime;

public interface Message extends Loggable {

    String getMessage();

    LocalDateTime getDateTime();

    boolean hasQuotedMessage();

    String getQuotedMessage();

}
