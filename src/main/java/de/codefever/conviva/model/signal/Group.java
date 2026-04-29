package de.codefever.conviva.model.signal;

/**
 * {
 *     "name": "Alternative F_ragstergruppe",
 *     "description": "Signale erkennen",
 *     "id": "group.yyy",
 *     "internal_id": "xxx",
 *     "members": [
 *       "xx"
 *     ],
 *     "blocked": false,
 *     "pending_invites": [],
 *     "pending_requests": [],
 *     "invite_link": "xx",
 *     "admins": [
 *       "xx"
 *     ]
 *   }
 */
public class Group {

    private String id;
    private String name;
    private String description;
    private String internalId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }
}
