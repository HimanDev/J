package com.example.himan.videotest;

/**
 * Created by himan on 29/7/16.
 */
public class Person {
    public Person(int id, String name, String phone,String code, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.code=code;
        this.email = email;
    }
    public Person(String name, String phone, String code,String email) {
        this.name = name;
        this.phone = phone;
        this.code=code;

        this.email = email;
    }

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private int id;
    String name,phone,email,code;
}