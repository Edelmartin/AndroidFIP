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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class DeleteMemberInGroupActivity extends AppCompatActivity {

    private Button deleteMemberInGroupButton = null;
    private Spinner memberNameSpinner = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private static ArrayList<String> listOfMember = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_member_in_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

        memberNameSpinner = (Spinner) findViewById(R.id.memberName_spinner);
        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        listOfMember = groupXMLManipulator.getListMemberOfGroup(groupName);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfMember);
        memberNameSpinner.setAdapter(adapter);

        deleteMemberInGroupButton = (Button) findViewById(R.id.deleteMemberInGroup_button);
        deleteMemberInGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Suppression des Transaction du Member
                ArrayList<Transaction> transactionListOfMember = new ArrayList<Transaction>();
                transactionListOfMember = groupXMLManipulator.getListTransactionOfMember(groupName, memberNameSpinner.getSelectedItem().toString());
                for (Transaction transaction: transactionListOfMember)
                {
                    groupXMLManipulator.deleteTransaction(groupName, transaction.getName());
                }

                //Suppression du Member
                groupXMLManipulator.deleteMemberOfGroup(groupName,memberNameSpinner.getSelectedItem().toString());

                //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
                Intent intent = new Intent(DeleteMemberInGroupActivity.this, GroupActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
    }

}
