package com.example.himan.videotest;

/**
 * Created by himan on 29/8/16.
 */
public class SettingsDto {

    private int idAnInt;
    private String message;

    public SettingsDto(int idAnInt, String message, int minutes) {
        this.idAnInt = idAnInt;
        this.message = message;
        this.minutes = minutes;
    }

    public SettingsDto() {
    }

    public SettingsDto(String message, int minutes) {
        this.message = message;
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getIdAnInt() {
        return idAnInt;
    }

    public void setIdAnInt(int idAnInt) {
        this.idAnInt = idAnInt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int minutes;

    @Override
    public String toString() {
        return "SettingsDto{" +
                "idAnInt=" + idAnInt +
                ", message='" + message + '\'' +
                ", minutes=" + minutes +
                '}';
    }
}
