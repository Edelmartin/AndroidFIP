package com.example.bricola.app_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AddGroupActivity extends AppCompatActivity {


    private Button addNewGroupButton = null;
    private Button addNewMemberButton = null;
    private Button deleteMemberButton = null;
    private EditText groupNameEditText = null;
    private EditText memberNameEditText = null;
    private LinearLayout newMemberLinearLayout = null;
    private XMLManipulator groupXMLManipulator;
    private LinearLayout memberDetailsLinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Ajout d'un nouveau groupe");

        groupNameEditText = (EditText) findViewById(R.id.groupName_editText);

        newMemberLinearLayout = (LinearLayout) findViewById(R.id.newMember_linearLayout);

        addNewGroupButton = (Button) findViewById(R.id.addNewGroup_button);
        addNewGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu du nom du groupe
                Boolean emptyEditText = false;
                if (groupNameEditText.getText().toString().matches(""))
                    emptyEditText = true;

                //Verification du contenu des noms des membres pour ne pas qu'ils soient vides
                for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++)
                {
                    Integer editTextField = 0;
                    memberDetailsLinearLayout = (LinearLayout) newMemberLinearLayout.getChildAt(i);
                    for (int j = 0; j < memberDetailsLinearLayout.getChildCount(); j++)
                    {
                        if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 0))
                        {
                            String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                            if (str.equals(""))
                                emptyEditText = true;
                            editTextField++;
                        }
                    }
                }

                if (emptyEditText) {
                    Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Récupération du nom et du numero des membres
                ArrayList<String> memberNameList = new ArrayList<String>();
                ArrayList<String> memberNumberList = new ArrayList<String>();

                for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++) {
                    Integer editTextField = 0;
                    memberDetailsLinearLayout = (LinearLayout) newMemberLinearLayout.getChildAt(i);
                    for (int j = 0 ; j < memberDetailsLinearLayout.getChildCount() ; j++)
                    {
                        if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 0)) {
                            String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                            memberNameList.add(str);
                            editTextField++;
                        }
                        else if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 1)) {
                            String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                            if (str.equals(""))
                                str = "null";
                            memberNumberList.add(str);
                        }
                    }
                }

                //Modification du fichier XML
                groupXMLManipulator = new XMLManipulator(getApplication());
                try {
                    groupXMLManipulator.addNewGroupWithMember(groupNameEditText.getText().toString(), memberNameList);
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }

                //Ouverture de la fenetre du groupe
                Intent intent = new Intent(AddGroupActivity.this, GroupActivity.class);
                intent.putExtra("groupName" , groupNameEditText.getText().toString());
                startActivity(intent);
            }
        });

        addNewMemberButton = (Button) findViewById(R.id.addNewMember_button);
        addNewMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (newMemberLinearLayout.getChildCount() == 1)
                {
                    deleteMemberButton.setEnabled(true);
                }
                if (newMemberLinearLayout.getChildCount() >=1)
                {
                    LinearLayout newMemberDetailsLinearLayout = new LinearLayout(getApplication());
                    newMemberDetailsLinearLayout.setOrientation(LinearLayout.VERTICAL);

                    TextView newMemberNameTextView = new TextView(getApplication());
                    newMemberNameTextView.setText("Nom:");
                    newMemberNameTextView.setTextColor(Color.parseColor("#7e7e7e"));
                    newMemberDetailsLinearLayout.addView(newMemberNameTextView);

                    EditText newMemberNameEditText = (EditText)getLayoutInflater().inflate(R.layout.newedittextstyle, null);
                    newMemberDetailsLinearLayout.addView(newMemberNameEditText);

                    TextView newMemberNumberTextView = new TextView(getApplication());
                    newMemberNumberTextView.setText("Numéro de téléphone:");
                    newMemberNumberTextView.setTextColor(Color.parseColor("#7e7e7e"));
                    newMemberDetailsLinearLayout.addView(newMemberNumberTextView);

                    EditText newMemberNumberEditText = (EditText)getLayoutInflater().inflate(R.layout.newedittextstyle, null);
                    newMemberNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                    newMemberDetailsLinearLayout.addView(newMemberNumberEditText);

                    newMemberLinearLayout.addView(newMemberDetailsLinearLayout);
                }
            }
        });

        deleteMemberButton = (Button) findViewById(R.id.deleteMember_button);
        deleteMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (newMemberLinearLayout.getChildCount() == 2)
                {
                    deleteMemberButton.setEnabled(false);

                }
                if (newMemberLinearLayout.getChildCount() > 1)
                {
                    newMemberLinearLayout.removeViewAt(newMemberLinearLayout.getChildCount() - 1);
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