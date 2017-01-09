package com.rohitw.model;


/**
 * Created by ROHITW on 3/9/2015.
 */
public class ChatData
{
    String chatUser;
    String chatReq;
    String chatResp;

    public String getChatReq() {
        return chatReq;
    }

    public void setChatReq(String chatReq) {
        this.chatReq = chatReq;
    }

    public String getChatResp() {
        return chatResp;
    }

    public void setChatResp(String chatResp) {
        this.chatResp = chatResp;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }
}
