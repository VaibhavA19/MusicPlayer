package com.codingblocks.VaibhavAlreja.MusicPlayer;


public class PedoUser {
    String steps ;
    String distance ;
    String date ;

    public PedoUser(String steps, String distance, String date) {
        this.steps = steps;
        this.distance = distance;
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public String getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

}
