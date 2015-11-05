package com.example.bricola.app_test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BalanceAccountActivity extends AppCompatActivity {

    private static ArrayList<String> donatorNamesList = new ArrayList<String>();
    private static ArrayList<String> receiverNamesList = new ArrayList<String>();
    private static ArrayList<String> valuesList = new ArrayList<String>();
    private static String groupName;
    private static LinearLayout linearLayoutVert;

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

            //Fleche
            TextView arrow = new TextView(this);
            arrow.setText(" paie à ");
            arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            arrow.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(arrow);

            //Nom du receveur
            TextView creditorNameTextView = new TextView(this);
            creditorNameTextView.setText(receiverNamesList.get(i));
            creditorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            creditorNameTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(creditorNameTextView);

            //Double point
            TextView colon = new TextView(this);
            colon.setText("\t:\t");
            colon.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            colon.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(colon);

            //Valer à trasnférer
            TextView valueTextView = new TextView(this);
            valueTextView.setText(valuesList.get(i) + " €");
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            valueTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(valueTextView);

            linearLayoutVert.addView(linearLayoutHor);
        }

    }

}
