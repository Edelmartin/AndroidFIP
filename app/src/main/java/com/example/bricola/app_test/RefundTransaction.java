package com.example.bricola.app_test;

/**
 * Created by Bricola on 29/10/2015.
 */
public class RefundTransaction {

    private String receiverName;
    private String donatorName;
    private Double value;

    RefundTransaction(String _receiverName, String _donatorName, Double _value)
    {
        receiverName = _receiverName;
        donatorName = _donatorName;
        value = _value;
    }

    public String getReceiverName() { return receiverName; }
    public String getDonatorName() { return donatorName; }
    public Double getValue() { return value; }
    public void setReceiverName(String _receiverName) { receiverName = _receiverName; }
    public void setDonatorName(String _donatorName) { donatorName = _donatorName; }
    public void setValue(Double _value) { value = _value; }
}
