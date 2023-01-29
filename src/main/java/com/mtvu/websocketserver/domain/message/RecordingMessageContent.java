package com.mtvu.websocketserver.domain.message;

public class RecordingMessageContent implements MessageContent {

    private int id;

    private long size;

    private String url;

    private int duration;

    @Override
    public MessageContentType getType() {
        return MessageContentType.RECORDING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
