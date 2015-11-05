package com.example.bricola.app_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddGroupActivity extends AppCompatActivity {


    private Button addNewGroupButton = null;
    private Button addNewMemberButton = null;
    private Button deleteMemberButton = null;
    private EditText groupNameEditText = null;
    private EditText memberNameEditText = null;
    private LinearLayout memberNameLinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Ajout d'un nouveau groupe");

        groupNameEditText = (EditText) findViewById(R.id.groupName_editText);

        memberNameLinearLayout = (LinearLayout) findViewById(R.id.memberName_linearLayout);

        addNewGroupButton = (Button) findViewById(R.id.addNewGroup_button);
        addNewGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu du nom du groupe
                Boolean emptyEditText = false;
                if (groupNameEditText.getText().toString().matches(""))
                    emptyEditText = true;

                //Verification du contenu des noms des membres
                for (int i = 0; i < memberNameLinearLayout.getChildCount(); i++)
                    if (memberNameLinearLayout.getChildAt(i) instanceof EditText) {
                        EditText myEditText = (EditText) memberNameLinearLayout.getChildAt(i);
                        if (myEditText.getText().toString().matches(""))
                            emptyEditText = true;
                    }

                if (emptyEditText) {
                    Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Retour a la fenetre principale en envoyant le nom du groupe et des membres en extra
                Intent intent = new Intent(AddGroupActivity.this, MainActivity.class);
                intent.putExtra("groupName", groupNameEditText.getText().toString());

                //Ajout du nom des membres dans les extra
                ArrayList<String> memberNameList = new ArrayList<String>();
                for (int i = 0; i < memberNameLinearLayout.getChildCount(); i++)
                    if (memberNameLinearLayout.getChildAt(i) instanceof EditText) {
                        String str = ((EditText) memberNameLinearLayout.getChildAt(i)).getText().toString();
                        memberNameList.add(str);
                    }

                intent.putStringArrayListExtra("memberList", memberNameList);
                startActivity(intent);
            }
        });

        addNewMemberButton = (Button) findViewById(R.id.addNewMember_button);
        addNewMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (memberNameLinearLayout.getChildCount() == 1)
                {
                    deleteMemberButton.setEnabled(true);
                }
                if (memberNameLinearLayout.getChildCount() >=1)
                {
                    EditText memberName = (EditText)getLayoutInflater().inflate(R.layout.newedittextstyle, null);
                    memberNameLinearLayout.addView(memberName);
                }
            }
        });

        deleteMemberButton = (Button) findViewById(R.id.deleteMember_button);
        deleteMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (memberNameLinearLayout.getChildCount() == 2)
                {
                    deleteMemberButton.setEnabled(false);

                }
                if (memberNameLinearLayout.getChildCount() > 1)
                {
                    memberNameLinearLayout.removeViewAt(memberNameLinearLayout.getChildCount() - 1);
                }
            }
        });
    }

    private ArrayList<String> separateMemberName (String memberNameString)
    {
        ArrayList <String> memberNameList = new ArrayList<String>();
        memberNameList.addAll(Arrays.asList(memberNameString.split(";")));
        return memberNameList;
    }
}