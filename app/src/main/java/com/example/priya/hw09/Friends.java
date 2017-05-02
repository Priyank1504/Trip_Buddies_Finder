package com.example.priya.hw09;

/**
 * Created by Priyank Verma
 */

public class Friends {
    String reqSentBy, reqSentTo;

    public String getReqSentBy() {
        return reqSentBy;
    }

    public void setReqSentBy(String reqSentBy) {
        this.reqSentBy = reqSentBy;
    }

    public String getReqSentTo() {
        return reqSentTo;
    }

    public void setReqSentTo(String reqSentTo) {
        this.reqSentTo = reqSentTo;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "reqSentBy='" + reqSentBy + '\'' +
                ", reqSentTo='" + reqSentTo + '\'' +
                '}';
    }
}
