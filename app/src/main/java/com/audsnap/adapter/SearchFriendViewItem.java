package com.audsnap.adapter;

/**
 * Created by SONI's on 8/18/2016.
 */
public class SearchFriendViewItem {

    private String username;
    private String status;
    private String photoUrl;
    private boolean isAdded;
    private String userKey;
    private String receivedImageUrl,receivedAudioUrl;
    private int number;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getReceivedImageUrl() {
        return receivedImageUrl;
    }

    public void setReceivedImageUrl(String receivedImageUrl) {
        this.receivedImageUrl = receivedImageUrl;
    }

    public String getReceivedAudioUrl() {
        return receivedAudioUrl;
    }

    public void setReceivedAudioUrl(String receivedAudioUrl) {
        this.receivedAudioUrl = receivedAudioUrl;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
