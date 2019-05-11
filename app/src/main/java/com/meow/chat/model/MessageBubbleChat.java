package com.meow.chat.model;

public class MessageBubbleChat {
    private int cnt;
    private String mes;

    public MessageBubbleChat() {
    }

    public MessageBubbleChat(int cnt, String mes) {
        this.cnt = cnt;
        this.mes = mes;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
