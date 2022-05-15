package org.rostik.andrusiv.lfu;

public enum Cause {
    LFU("least frequently used"),
    EXPIRED("Expired");

    private String cause;

    Cause(String cause) {
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

}
