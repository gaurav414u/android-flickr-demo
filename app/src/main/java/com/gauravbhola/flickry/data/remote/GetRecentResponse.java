package com.gauravbhola.flickry.data.remote;


public class GetRecentResponse {
    private String stat;
    private String message;
    private String code;
    private PhotosResponse photos;

    public PhotosResponse getPhotos() {
        return photos;
    }

    public void setPhotos(PhotosResponse photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
