package com.example.priya.hw09;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by priyank Verma
 */
public class Trip {
    String tripName, from, to, comment, group, startDate, imgUri, userid;

    ArrayList<Location> locList = new ArrayList<>();

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<Location> getLocList() {
        return locList;
    }

    public void setLocList(ArrayList<Location> locList) {
        this.locList = locList;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripName='" + tripName + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", comment='" + comment + '\'' +
                ", group='" + group + '\'' +
                ", startDate='" + startDate + '\'' +
                ", imgUri='" + imgUri + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
