package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddGroupActivity extends AppCompatActivity {


    private Button addNewGroupButton = null;
    private Button addNewMemberButton = null;
    private EditText groupNameEditText = null;
    private EditText memberNameEditText = null;
    private static Integer nbOfMember = 1;
    private static String nameLastEditText = "member01_editText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Ajout d'un nouveau groupe");

        groupNameEditText = (EditText) findViewById(R.id.groupName_editText);

        addNewGroupButton = (Button) findViewById(R.id.addNewGroup_button);
        addNewGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu des EditText qui ne doivent pas êter vides
                RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.contentAddGroupLayout);
                for(int i = 0; i < mRlayout.getChildCount(); i++)
                    if(mRlayout.getChildAt(i) instanceof EditText)
                    {
                        EditText myEditText = (EditText) mRlayout.getChildAt(i);
                        if (myEditText.getText().toString().matches(""))
                        {
                            Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                //Retour a la fenetre principale en envoyant le nom du groupe et des membres en extra
                Intent intent = new Intent(AddGroupActivity.this, MainActivity.class);
                intent.putExtra("groupName", groupNameEditText.getText().toString());
                memberNameEditText = (EditText) findViewById(R.id.memberName_editText);
                ArrayList<String> memberNameList = separateMemberName(memberNameEditText.getText().toString());
                intent.putStringArrayListExtra("memberList", memberNameList);
                startActivity(intent);
            }
        });
        /*
        addNewMemberButton = (Button) findViewById(R.id.addNewMember_button);
        addNewMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nbOfMember++;
                RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.contentAddGroupLayout);
                RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                EditText myEditText = new EditText(getApplication());
                myEditText.setLayoutParams(mRparams);
                myEditText.setId(nbOfMember);
                mRparams.addRule(RelativeLayout.BELOW, R.id.memberName_editText);
                mRlayout.addView(myEditText, mRparams);
            }
        });
        */

    }

    private ArrayList<String> separateMemberName (String memberNameString)
    {
        ArrayList <String> memberNameList = new ArrayList<String>();
        memberNameList.addAll(Arrays.asList(memberNameString.split(";")));
        return memberNameList;
    }
}