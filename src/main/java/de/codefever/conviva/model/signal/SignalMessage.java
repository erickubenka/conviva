package de.codefever.conviva.model.signal;

import de.codefever.conviva.model.general.Message;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider.PROPERTY_MANAGER;

/**
 * {
 *     "envelope": {
 *       "source": "+xx",
 *       "sourceNumber": "+xx",
 *       "sourceUuid": "xx",
 *       "sourceName": "xx",
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
 *           "author": "+xx",
 *           "authorNumber": "+xx",
 *           "authorUuid": "xx",
 *           "text": "Die sollte er ja easy ausführen können",
 *           "attachments": []
 *         },
 *         "groupInfo": {
 *           "groupId": "xx",
 *           "groupName": "xx",
 *           "revision": 49,
 *           "type": "DELIVER"
 *         },
 *         attachments":[
 *             {
 *                "contentType":"image/png",
 *                "filename":null,
 *                "id":"Td0m0WtATCxwFtXl40q3.png",
 *                "size":145165,
 *                "width":600,
 *                "height":894,
 *                "caption":null,
 *                "uploadTimestamp":1772525697995
 *             }
 *          ]
 *       }
 *     },
 *     "account": "+xx"
 *   }
 */
public class SignalMessage implements Message {

    private String sourceName;
    private Long timestamp;
    private LocalDateTime timestampDateTime;

    private GroupInfo groupInfo;
    private QuotedMessage quote;

    // splitted on \n
    private String message;

    // List of Attachments
    private List<Attachment> attachments;

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

    public String getQuotedMessage() {
        return this.quote != null ? this.quote.getMessage() : null;
    }

    @Override
    public void setQuotedMessage(String message) {
        throw new RuntimeException("Not implemented");
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

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
