package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import android.widget.Toast;


import java.util.ArrayList;



public class EditMemberActivity extends AppCompatActivity {
    private static String memberName,groupName;
    private static String initialMemberName,initialContact;
    private static EditText ContactView = null;
    private static EditText MemberView = null;
    private static ArrayList<Transaction> transactionList = new ArrayList<Transaction> ();

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

        //initialise le chmap nom du membre
        initialMemberName = memberName;
        MemberView.setText(initialMemberName);

        // test tous les "contacts" du groupe
        for (int i=0 ; i < listMember.size() ; i++) {

            //si le contact correspond a celui sur lequel on a cliqué
            if (listMember.get(i).toString().equals(memberName)) {
                //on initialise le 2eme edit text
                initialContact = contactList.get(i).toString();
                ContactView.setText(contactList.get(i).toString());
            }
        }
        //mettre en place la modification

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_element, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addMember) {
            modifyMember();
        }
        return super.onOptionsItemSelected(item);
    }

    public void modifyMember ()
    {
        //Verification du contenu des EditText qui ne doivent pas être vides
        if( !MemberView.getText().toString().matches("") && !ContactView.getText().toString().matches("") ) {
            //si l'un des 2 champs a été modifié
            if (!MemberView.getText().toString().matches(initialMemberName) || !ContactView.getText().toString().matches(initialContact)) {

                XMLManipulator xmlmanip = new XMLManipulator(EditMemberActivity.this);

                //si le nom du membre a changé
                if (!MemberView.getText().toString().matches(initialMemberName)) {
                    xmlmanip.modifyMemberName(groupName, initialMemberName, MemberView.getText().toString());
                    //il faut réaffcecter le nom correct a toutes les transactions
                    transactionList = xmlmanip.getListTransactionOfMember(groupName,initialMemberName);
                    for(Transaction transaction: transactionList)
                        xmlmanip.modifyTransactionOwner(groupName,transaction.getName(),MemberView.getText().toString());
                }
                //si le contact a changés
                if (!ContactView.getText().toString().matches(initialContact)) {
                    xmlmanip.modifyMemberContact(groupName, MemberView.getText().toString(), ContactView.getText().toString());
                }
                Toast.makeText(getApplication(), "Membre modifié", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplication(), "Aucune modification apportée", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplication(), "Vous avez mal compléter une zone de texte", Toast.LENGTH_SHORT).show();
            return;
        }
        //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
        Intent intent = new Intent(EditMemberActivity.this, GroupActivity.class);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }


}