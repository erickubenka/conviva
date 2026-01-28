package de.codefever.conviva.model.signal;

/**
 * {
 *     "name": "Alternative F_ragstergruppe",
 *     "description": "Signale erkennen",
 *     "id": "group.VStzSko4em83QzZzY1pySkF5RG9GQXNNKzRNeWpKOWZDS0NWNDJjaExzRT0=",
 *     "internal_id": "U+sJJ8zo7C6scZrJAyDoFAsM+4MyjJ9fCKCV42chLsE=",
 *     "members": [
 *       "71024020-2fe3-4536-aa71-ef8e1c8b3967",
 *       "+4915142324728",
 *       "8db6d1cd-7a4c-42a0-b9cc-167e784583b9",
 *       "13220659-892a-4138-92e4-8ddd53a8d22f",
 *       "15f7c850-d694-4583-8574-ae3156554932",
 *       "+4917680242682",
 *       "398f9b81-31fb-438b-8b16-dd8af198c138",
 *       "b692fad5-e8d3-4eb8-857e-18fc5eb54bb4",
 *       "614a6a83-e548-4bb2-bdf2-92de0a8c7eef",
 *       "+4915234347105",
 *       "15fd1954-ec27-4f23-b5d1-4f7943d1d9fc",
 *       "0bd44950-d318-463e-8da7-692de96e9f77",
 *       "0e27564c-47a9-4cd2-b615-cd13af5e795d",
 *       "a91dff52-ce1e-47dc-8bbb-03bd1508a48c",
 *       "+491628293597",
 *       "9c970b5c-0a08-4e75-a985-ab75c2795f85",
 *       "8e3a9db7-08a1-4f45-8905-06a9ae229103",
 *       "6408a99d-c873-4933-95a5-0db23e1577f3",
 *       "9a4a12c7-5182-45a5-a2e0-27c1c544387f",
 *       "5c894ac7-7ac0-4443-b1cc-9ffb792caec1",
 *       "d03df84a-deaa-4c1d-be19-031dd6bfba39"
 *     ],
 *     "blocked": false,
 *     "pending_invites": [],
 *     "pending_requests": [],
 *     "invite_link": "https://signal.group/#CjQKIA4TsABtmdn7J6XGfm9p30_s4rhKUOgs08y2Zvd3FuaeEhALzOcJWMrikGbdD3EIzDbb",
 *     "admins": [
 *       "15fd1954-ec27-4f23-b5d1-4f7943d1d9fc"
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
