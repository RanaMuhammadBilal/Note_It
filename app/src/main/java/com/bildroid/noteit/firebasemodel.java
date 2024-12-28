package com.bildroid.noteit;

import com.google.firebase.Timestamp;

public class firebasemodel {

    public String title;
    public   String content;
    public Timestamp timestamp;

    public firebasemodel(){

    }

    public firebasemodel(String title, String content){

        this.title=title;
        this.content=content;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Object setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return null;
    }
}
