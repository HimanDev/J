package com.example.himan.videotest.domains;

/**
 * Created by himan on 29/7/16.
 */
public class PersonDto {

    public static final String TABLE_CONTACTS = "person";

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone";
    public static final String KEY_E_MAIL = "email";
    public static final String KEY_CODE = "code";



    public PersonDto(int id, String name, String phone, String code, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.code=code;
        this.email = email;
    }
    public PersonDto(String name, String phone, String code, String email) {
        this.name = name;
        this.phone = phone;
        this.code=code;

        this.email = email;
    }

    public PersonDto() {
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
