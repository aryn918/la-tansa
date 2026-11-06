package com.latansa.app;

public class Task {
    public long id;
    public String title;
    public String note;
    public long time;
    public String repeat;
    public boolean done;
    public Task(long id, String title, String note, long time, String repeat, boolean done) {
        this.id=id; this.title=title; this.note=note; this.time=time; this.repeat=repeat; this.done=done;
    }
}
