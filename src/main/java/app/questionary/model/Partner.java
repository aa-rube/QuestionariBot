package app.questionary.model;

import java.time.LocalDate;

public class Partner {
    private String groupUserName;
    private String groupUrl;
    private LocalDate expiredPartnerShipTime;

    public Partner() {
    }

    public Partner(String groupUserName, String groupUrl, LocalDate expiredPartnerShipTime) {
        this.groupUserName = groupUserName;
        this.groupUrl = groupUrl;
        this.expiredPartnerShipTime = expiredPartnerShipTime;
    }

    public LocalDate getExpiredPartnerShipTime() {
        return expiredPartnerShipTime;
    }

    public void setExpiredPartnerShipTime(LocalDate expiredPartnerShipTime) {
        this.expiredPartnerShipTime = expiredPartnerShipTime;
    }

    public String getGroupUserName() {
        return groupUserName;
    }

    public void setGroupUserName(String groupUserName) {
        this.groupUserName = groupUserName;
    }

    public String getGroupUrl() {
        return groupUrl;
    }

    public void setGroupUrl(String groupUrl) {
        this.groupUrl = groupUrl;
    }
}