package com.example.bricola.app_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by nico on 08/11/2015.
 */

// A tester sur un téléphone

public class SendMail{

    private String message;
    private String address;
    private String nomGroupe;

    public SendMail (String _message, String _address, String _nomGroupe,Context activityContext) {

      message = _message;
      address = _address;
      nomGroupe = _nomGroupe;


        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        email.putExtra(Intent.EXTRA_CC, address);
        email.putExtra(Intent.EXTRA_SUBJECT, nomGroupe);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activityContext.startActivity(email);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activityContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }



}
