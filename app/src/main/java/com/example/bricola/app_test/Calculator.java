package com.example.bricola.app_test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Bricola on 29/10/2015.
 */
public class Calculator {

    public ArrayList<RefundTransaction> calculateRefundTransactions (ArrayList<Member> memberList) throws ParseException {
        ArrayList<RefundTransaction> refundTransactionList = new ArrayList<RefundTransaction>();

        //Opérateur de formatage pour arrondir les valeurs
        DecimalFormat df = new DecimalFormat("#.##");
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number number;

        //Calcul du nombre de membre dans la liste
        Integer numberOfMember = memberList.size();

        //Calcul du total des transactions
        Double totalTransactionAmout = 0.0;
        for (Member member: memberList)
            totalTransactionAmout += member.getTotalTransaction();
        number = format.parse(df.format(totalTransactionAmout));
        totalTransactionAmout = number.doubleValue();

        //Calcul de la dépense moyenne par membre
        Double averageTransactionAmount = totalTransactionAmout/numberOfMember;
        number = format.parse(df.format(averageTransactionAmount));
        averageTransactionAmount = number.doubleValue();

        //Création des transaction de remboursement pour équilibrer les dépenses
        for (int i = 0 ; i < numberOfMember ; i++)
        {
            while (!memberList.get(i).isOk(averageTransactionAmount))
            {
                if (memberList.get(i).isUnder(averageTransactionAmount))
                {
                    for (int j = i+1 ; j < numberOfMember ; j++)
                    {
                        Double lack = memberList.get(i).getLack(averageTransactionAmount);
                        number = format.parse(df.format(lack));
                        lack = number.doubleValue();
                        if(memberList.get(j).isUpper(averageTransactionAmount))
                        {
                            Double excess = memberList.get(j).getExcess(averageTransactionAmount);
                            number = format.parse(df.format(excess));
                            excess = number.doubleValue();
                            if (excess >= lack)
                            {
                                memberList.get(i).add(lack);
                                memberList.get(j).remove(lack);
                                refundTransactionList.add(new RefundTransaction(memberList.get(j).getName(), memberList.get(i).getName(), lack));
                            }
                            else
                            {
                                memberList.get(i).add(excess);
                                memberList.get(j).remove(excess);
                                refundTransactionList.add(new RefundTransaction(memberList.get(j).getName(), memberList.get(i).getName(), excess));
                            }
                        }
                    }
                }
                else
                {
                    for (int j = i+1 ; j < numberOfMember ; j++)
                    {
                        Double excess = memberList.get(i).getExcess(averageTransactionAmount);
                        number = format.parse(df.format(excess));
                        excess = number.doubleValue();
                        if(memberList.get(j).isUnder(averageTransactionAmount))
                        {
                            Double lack = memberList.get(j).getLack(averageTransactionAmount);
                            number = format.parse(df.format(lack));
                            lack = number.doubleValue();
                            if (excess >= lack)
                            {
                                memberList.get(i).remove(lack);
                                memberList.get(j).add(lack);
                                refundTransactionList.add(new RefundTransaction(memberList.get(i).getName(), memberList.get(j).getName(), lack));
                            }
                            else
                            {
                                memberList.get(i).remove(excess);
                                memberList.get(j).add(excess);
                                refundTransactionList.add(new RefundTransaction(memberList.get(i).getName(), memberList.get(j).getName(), excess));
                            }
                        }
                    }
                }
            }
        }
        return refundTransactionList;
    }

}
