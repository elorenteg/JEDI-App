package com.esterlorente.jediapp;

public class Score {
    private String username;
    private int score;

    Score(String username, int score) {
        this.username = username;
        this.score = score;
    }

    Score() {
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
