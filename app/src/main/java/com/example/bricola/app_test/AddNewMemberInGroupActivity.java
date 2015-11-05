package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class AddNewMemberInGroupActivity extends AppCompatActivity {

    private Button addNewMemberInGroupButton = null;
    private EditText memberNameEditText = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_in_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        memberNameEditText = (EditText) findViewById(R.id.memberName_editText);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

        addNewMemberInGroupButton = (Button) findViewById(R.id.addNewMemberInGroup_button);
        addNewMemberInGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu des EditText qui ne doivent pas être vides
                RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.contentAddNewMemberInGroup);
                for (int i = 0; i < mRlayout.getChildCount(); i++)
                    if (mRlayout.getChildAt(i) instanceof EditText) {
                        EditText myEditText = (EditText) mRlayout.getChildAt(i);
                        if (myEditText.getText().toString().matches("")) {
                            Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                String newMemberName = memberNameEditText.getText().toString();
                groupXMLManipulator = new XMLManipulator(getApplicationContext());
                groupXMLManipulator.addNewMemberInGroup(groupName, newMemberName);

                //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
                Intent intent = new Intent(AddNewMemberInGroupActivity.this, GroupActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
    }

}
