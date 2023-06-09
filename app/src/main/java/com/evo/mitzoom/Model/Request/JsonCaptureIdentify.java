package com.evo.mitzoom.Model.Request;

import com.google.gson.annotations.SerializedName;

public class JsonCaptureIdentify {

    @SerializedName("image")
    private String image;

    @SerializedName("idDips")
    private String idDips;

    public String getIdDips() {
        return idDips;
    }

    public void setIdDips(String idDips) {
        this.idDips = idDips;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
