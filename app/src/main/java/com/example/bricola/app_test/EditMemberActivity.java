package com.example.bricola.app_test;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class EditMemberActivity extends AppCompatActivity {
    private static String memberName,groupName;
    public String message;
    private static EditText ContactView = null;
    private static EditText MemberView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        memberName = extras.getString("memberName");
        groupName = extras.getString("groupName");

        this.setTitle("Membre");

        MemberView = (EditText) findViewById(R.id.memberName_editText);
        ContactView = (EditText) findViewById(R.id.memberContact_editText);

        //Récupération de la liste des numéros
        XMLManipulator xmlmanip = new XMLManipulator(EditMemberActivity.this);
        ArrayList<String> listMember = xmlmanip.getListMemberOfGroup(groupName);
        ArrayList<String> contactList = xmlmanip.getListMemberContact(groupName);
        String listeAddrMail ="";
        String listeNomContactSansContact = "";

        MemberView.setText(memberName);

        //fon test tous les "contacts" du groupe
        for (int i=0 ; i < listMember.size() ; i++) {

            //si le contact coorespond a celui sur lequel on a cliqué
            if (listMember.get(i).toString().equals(memberName)) {
                //on initialise le 2eme edit text
                ContactView.setText(contactList.get(i).toString());
            }
        }
        //mettre en place la modification

    }

}
