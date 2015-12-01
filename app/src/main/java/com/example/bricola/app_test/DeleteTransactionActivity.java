package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class DeleteTransactionActivity extends AppCompatActivity {

    private Button deleteTransactionButton = null;
    private Spinner transactionNameSpinner = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private static ArrayList<Transaction> listOfTransaction = new ArrayList<Transaction>();
    private static ArrayList<String> transactionNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

        //Récupération de la list des transactions du group
        transactionNameSpinner = (Spinner) findViewById(R.id.transactionName_spinner);
        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        listOfTransaction = groupXMLManipulator.getListTransactionOfGroup(groupName);
        transactionNameList.clear();
        for (Transaction transaction: listOfTransaction)
            transactionNameList.add(transaction.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, transactionNameList);
        transactionNameSpinner.setAdapter(adapter);

        deleteTransactionButton = (Button) findViewById(R.id.deleteTransaction_button);
        deleteTransactionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu des EditText qui ne doivent pas être vides
                //Verification a faire sur le Spinner
                /*RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.contentAddNewMemberInGroup);
                for (int i = 0; i < mRlayout.getChildCount(); i++)
                    if (mRlayout.getChildAt(i) instanceof EditText) {
                        EditText myEditText = (EditText) mRlayout.getChildAt(i);
                        if (myEditText.getText().toString().matches("")) {
                            Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    */

                groupXMLManipulator.deleteTransaction(groupName, transactionNameSpinner.getSelectedItem().toString());

                //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
                Intent intent = new Intent(DeleteTransactionActivity.this, GroupActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
                finish();
            }
        });
    }

}