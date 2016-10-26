package com.example.himan.videotest.form;

/**
 * Created by DPandey on 11-10-2016.
 */
public interface PaymentForm {
    public String getCardNumber();
    public String getCvc();
    public Integer getExpMonth();
    public Integer getExpYear();
    public String getCurrency();
    public String getAmount();
}
