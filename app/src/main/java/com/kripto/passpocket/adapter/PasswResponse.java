package com.kripto.passpocket.adapter;

public class PasswResponse {
    private String socialMedia;
    private String username;
    private String password;

    public PasswResponse() {
    }

    public PasswResponse(String socialMedia, String username, String password) {
        this.socialMedia = socialMedia;
        this.username = username;
        this.password = password;
   }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
