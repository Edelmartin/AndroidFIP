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
    private static LinearLayout buttonLinearLayout;
    public String message;


    @Override
    public void onBackPressed() {
        Intent retour = new Intent(BalanceAccountActivity.this, GroupActivity.class);
        retour.putExtra("groupName" , groupName);
        startActivity(retour);
        finish();
    }

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
        message = "Bonjour à tous,\n"
                + "Vous trouverez ci dessous les comptes calculés automatiquement à l'aide de l'application \"Partage de frais\" : \n"
                + "Groupe : " + groupName + "\n";

        this.setTitle("Comptes");//groupName);

        linearLayoutVert = (LinearLayout) findViewById(R.id.vertical_linearLayout);
        Integer textSize = 18;
        for (int i = 0 ; i < donatorNamesList.size() ; i++ )
        {
            LinearLayout linearLayoutHor = new LinearLayout(this);
            linearLayoutHor.setOrientation(LinearLayout.VERTICAL);
            linearLayoutHor.setPadding(0, 15, 0, 15);
            linearLayoutHor.setGravity(Gravity.CENTER);

            //Nom du donateur
            TextView debtorNameTextView = new TextView(this);
            debtorNameTextView.setText(donatorNamesList.get(i) + " doit à");
            debtorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            debtorNameTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(debtorNameTextView);
            message=message+donatorNamesList.get(i)+" doit payer à ";

            //Nom du receveur et la somme due
            TextView creditorNameTextView = new TextView(this);
            creditorNameTextView.setText(receiverNamesList.get(i) + " : " + valuesList.get(i) + " €");
            creditorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            creditorNameTextView.setTextColor(Color.parseColor("#000000"));
            linearLayoutHor.addView(creditorNameTextView);
            message=message+receiverNamesList.get(i)+ " : " + valuesList.get(i) + " €\n";

            linearLayoutVert.addView(linearLayoutHor);

        }

        /*messageSender = (Button) findViewById(R.id.MessageSender);
        messageSender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {*/

        buttonLinearLayout = (LinearLayout) findViewById(R.id.button_linearLayout);
        buttonLinearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Récupération de la liste des numéros:

                XMLManipulator xmlmanip = new XMLManipulator(BalanceAccountActivity.this);
                ArrayList<String> listMember = xmlmanip.getListMemberOfGroup(groupName);
                ArrayList<String> contactList = xmlmanip.getListMemberContact(groupName);
                String listeAddrMail ="";
                String listeNomContactSansContact = "";

                //fon test tous les "contacts" du groupe
                for (int i=0 ; i < listMember.size() ; i++) {

                    //si le contact n'est pas vide, on envoi soit un message soit un email
                    if (!contactList.get(i).toString().equals("")) {
                        //si pas @ c'est un numéro
                        if (contactList.get(i).indexOf("@") <= -1) {
                            SendSMS sms = new SendSMS(contactList.get(i), message);
                        }

                        //sinon on récupere l'adresse mail et on envoie à la fin de la boucle
                        // à cause de l'ouverture de l'ouverture de l'apli mail
                        else {
                            listeAddrMail = listeAddrMail + contactList.get(i) + ";";
                        }
                    }

                    //si on n'a ni numéros ni adresse mail
                    else {
                        //on affiche un message avec les noms des personnes desquelles
                        //nous n'avons pas de mail et de message
                        listeNomContactSansContact = listeNomContactSansContact + listMember.get(i) + "\n";
                    }


                    if(!listeAddrMail.isEmpty()) {
                        SendMail mail = new SendMail(message, listeAddrMail, "comptes", BalanceAccountActivity.this);
                    }
                }

                if (!listeNomContactSansContact.isEmpty()) {//C'est personnes n'ont pas pu etre contacté(es) car nous ne possédons pas d'adresse mail ou de numéros de téléphone
                    Toast toast = Toast.makeText(getApplicationContext(), "Manque d'information pour les membres suivants:\n"+listeNomContactSansContact , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(18);
                    toast.show();
                }
            }
        });
    }


}
