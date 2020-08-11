package com.tranthibich.chatapp;

import java.net.PortUnreachableException;

public class Contacst {

    public String name, status, image;

    public Contacst(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public Contacst()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
