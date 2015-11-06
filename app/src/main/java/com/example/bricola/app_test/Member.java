package com.example.bricola.app_test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by Bricola on 29/10/2015.
 */
public class Member {

    private String name;
    private Double totalTransaction;
    private DecimalFormat df = new DecimalFormat("#.##");
    private NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
    private Number number;

    public Member(String _name, Double _totalTransaction) throws ParseException {
        name = _name;
        number = format.parse(df.format(_totalTransaction));
        totalTransaction = number.doubleValue();
    }

    public String getName() {return name; }
    public Double getTotalTransaction() { return totalTransaction; }

    public void add(Double amount) throws ParseException {
        number = format.parse(df.format(totalTransaction + amount));
        totalTransaction = number.doubleValue();
    }

    public void remove(Double amount) throws ParseException {
        number = format.parse(df.format(totalTransaction - amount));
        totalTransaction = number.doubleValue();
    }

    public Boolean isOk(Double averageTransactionAmount, Integer numberOfMember) throws ParseException {
        //Limite dynamique = (1 centime) x (Nb Participants)
        Double limitdynamic = 0.01 * numberOfMember;
        number = format.parse(df.format(averageTransactionAmount + limitdynamic));
        Double topLimit = number.doubleValue();

        number = format.parse(df.format(averageTransactionAmount - limitdynamic));
        Double bottomLimit = number.doubleValue();

        if ((totalTransaction <= topLimit) && (totalTransaction >= bottomLimit))
            return true;
        else
            return false;
    }

    public Boolean isUpper(Double averageTransactionAmount)
    {
        if (totalTransaction > averageTransactionAmount)
            return true;
        else
            return false;
    }

    public Boolean canAfford(Double averageTransactionAmount)
    {
        if ((totalTransaction-averageTransactionAmount) >= averageTransactionAmount)
            return true;
        else
            return false;
    }

    public Boolean isUnder(Double averageTransactionAmount)
    {
        if (totalTransaction < averageTransactionAmount)
            return true;
        else
            return false;
    }

    public Double getLack(Double averageTransactionAmount)
    {
        return averageTransactionAmount - totalTransaction;
    }

    public Double getExcess(Double averageTransactionAmount)
    {
        return totalTransaction - averageTransactionAmount;
    }
}
