package de.codefever.conviva.model.signal;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import static eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider.PROPERTY_MANAGER;

/**
 * {
 *     "envelope": {
 *       "source": "+4915142324728",
 *       "sourceNumber": "+4915142324728",
 *       "sourceUuid": "5449c4ef-ce34-4bc8-be3a-4c404fab0edf",
 *       "sourceName": "Eric",
 *       "sourceDevice": 1,
 *       "timestamp": 1769528053253,
 *       "serverReceivedTimestamp": 1769528053373,
 *       "serverDeliveredTimestamp": 1769528058018,
 *       "dataMessage": {
 *         "timestamp": 1769528053253,
 *         "message": "Habe es ihm mal getextet",
 *         "expiresInSeconds": 0,
 *         "isExpirationUpdate": false,
 *         "viewOnce": false,
 *         "quote": {
 *           "id": 1769527117081,
 *           "author": "+4915142324728",
 *           "authorNumber": "+4915142324728",
 *           "authorUuid": "5449c4ef-ce34-4bc8-be3a-4c404fab0edf",
 *           "text": "Die sollte er ja easy ausführen können",
 *           "attachments": []
 *         },
 *         "groupInfo": {
 *           "groupId": "U+sJJ8zo7C6scZrJAyDoFAsM+4MyjJ9fCKCV42chLsE=",
 *           "groupName": "Alternative F_ragstergruppe",
 *           "revision": 49,
 *           "type": "DELIVER"
 *         }
 *       }
 *     },
 *     "account": "+491628293597"
 *   }
 */
public class Message {

    private String sourceName;
    private Long timestamp;
    private LocalDateTime timestampDateTime;

    private GroupInfo groupInfo;
    private QuotedMessage quote;

    // splitted on \n
    private String message;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;

        final Date date = new Date(timestamp);
        this.timestampDateTime = LocalDateTime.ofInstant(date.toInstant(), TimeZone.getTimeZone(PROPERTY_MANAGER.getProperty("conviva.bot.timezone.target")).toZoneId());
    }

    public LocalDateTime getDateTime() {
        return timestampDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public QuotedMessage getQuote() {
        return quote;
    }

    public void setQuote(QuotedMessage quote) {
        this.quote = quote;
    }

    public boolean hasMessage() {
        return this.message != null && !this.message.isEmpty();
    }

    public boolean hasQuotedMessage() {
        return this.quote != null;
    }

    public boolean isFromGroup() {
        return this.groupInfo != null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timestampDateTime=" + timestampDateTime +
                ", message='" + message + '\'' +
                '}';
    }
}
