package com.gaoshiqi.chatgirlfriend.bean;

public class Msg {
    private final static int type_recrived = 0;
    private final static int type_send = 1;
    private String content;
    private MessageType type;

    public Msg(String content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public enum MessageType {
        USER,
        GPT
    }
}
