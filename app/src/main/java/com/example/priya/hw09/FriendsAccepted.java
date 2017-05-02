package com.example.priya.hw09;

/**
 * Created by Priyank Verma
 */

public class FriendsAccepted {
    String frinedIs, friendWhom;

    public String getFrinedIs() {
        return frinedIs;
    }

    public void setFrinedIs(String frinedIs) {
        this.frinedIs = frinedIs;
    }

    public String getFriendWhom() {
        return friendWhom;
    }

    public void setFriendWhom(String friendWhom) {
        this.friendWhom = friendWhom;
    }

    @Override
    public String toString() {
        return "FriendsAccepted{" +
                "frinedIs='" + frinedIs + '\'' +
                ", friendWhom='" + friendWhom + '\'' +
                '}';
    }
}
