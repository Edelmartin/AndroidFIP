package com.example.bricola.app_test;

import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by nico on 05/11/2015.
 */
public class SendSMS {

    private String msg;
    private String num;

    public SendSMS(String _num, String _msg){
        num = _num;
        msg = _msg;

/*        //changement de méthode, sans constructeur
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(num, null, msg, null, null);
        //SmsManager.getDefault().sendTextMessage(num, null, msg, null, null);
*/
        SmsManager sms = SmsManager.getDefault();

        ArrayList<String> parts = sms.divideMessage(msg);
        sms.sendMultipartTextMessage(num, null, parts, null, null);
    }
}
