package com.example.bricola.app_test;

import java.util.Date;

/**
 * Created by Bricola on 23/10/2015.
 */
public class Transaction {

    private String name;
    private String owner;
    private Double value;
    private Date date;

    Transaction(String _name, String _owner, Double _value, Date _date) {
        name = _name;
        owner = _owner;
        value = _value;
        date = _date;
    }

    public String getName() { return name; }
    public void setName(String _name) { name = _name; }
    public String getOwner() { return owner; }
    public void setOwner(String _owner) { owner = _owner; }
    public Double getValue() { return value; }
    public void setValue(Double _value) { value = _value; }
    public Date getDate() { return date; }
    public void setDate(Date _date) { date = _date; }
}
