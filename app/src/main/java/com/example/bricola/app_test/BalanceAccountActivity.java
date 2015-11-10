package com.example.bricola.app_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BalanceAccountActivity extends AppCompatActivity {

    private static ArrayList<String> donatorNamesList = new ArrayList<String>();
    private static ArrayList<String> receiverNamesList = new ArrayList<String>();
    private static ArrayList<String> valuesList = new ArrayList<String>();
    private static String groupName;
    private static LinearLayout linearLayoutVert;
    private static Button messageSender;
    public String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        receiverNamesList = extras.getStringArrayList("receiverNames");
        donatorNamesList = extras.getStringArrayList("donatorNames");
        valuesList = extras.getStringArrayList("values");
        message ="Bonjour à tous,\nVous trouverez ci dessous les comptes calculés automatiquement" +
                "à l'aide de l'application GroupeAcountCompta :\n";

        this.setTitle(groupName);

        linearLayoutVert = (LinearLayout) findViewById(R.id.vertical_linearLayout);
        Integer textSize = 18;
        for (int i = 0 ; i < donatorNamesList.size() ; i++ )
        {
            LinearLayout linearLayoutHor = new LinearLayout(this);
            linearLayoutHor.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutHor.setPadding(0, 15, 0, 15);
            linearLayoutHor.setGravity(Gravity.CENTER);

            //Nom du donateur
            TextView debtorNameTextView = new TextView(this);
            debtorNameTextView.setText(donatorNamesList.get(i));
            debtorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            debtorNameTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(debtorNameTextView);
            message=message+donatorNamesList.get(i);

            //Fleche
            TextView arrow = new TextView(this);
            arrow.setText(" doit à ");
            arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            arrow.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(arrow);
            message=message+" doit à ";

            //Nom du receveur
            TextView creditorNameTextView = new TextView(this);
            creditorNameTextView.setText(receiverNamesList.get(i));
            creditorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            creditorNameTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(creditorNameTextView);
            message=message+receiverNamesList.get(i);

            //Double point
            TextView colon = new TextView(this);
            colon.setText("\t:\t");
            colon.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            colon.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(colon);
            message=message+"\t:\t";

            //Valeur à transférer
            TextView valueTextView = new TextView(this);
            valueTextView.setText(valuesList.get(i) + " €");
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            valueTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(valueTextView);
            message=message+valuesList.get(i) + " €\n";

            linearLayoutVert.addView(linearLayoutHor);

        }

        messageSender = (Button) findViewById(R.id.MessageSender);
        messageSender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Avec le num à Martin :p
                SendSMS sms= new SendSMS("0606419410",message);
                SendMail mail = new SendMail(message,"christnachern@aol.com","comptes",BalanceAccountActivity.this );

            }
        });
    }

}
