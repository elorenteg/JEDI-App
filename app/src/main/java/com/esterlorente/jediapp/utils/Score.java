package com.esterlorente.jediapp.utils;

public class Score {
    private byte[] image;
    private String username;
    private int score;

    public Score(byte[] image, String username, int score) {
        this.image = image;
        this.username = username;
        this.score = score;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
