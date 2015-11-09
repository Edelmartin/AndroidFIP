package com.example.bricola.app_test;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

public class AddNewMemberInGroupActivity extends AppCompatActivity {

    public static final int PICK_CONTACT    = 1;

    private Button addNewMemberInGroupButton = null;
    private Button addNewMemberButton = null;
    private Button addNewMemberRepertory = null;
    private Button deleteMemberButton = null;
    private EditText memberNameEditText = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private LinearLayout newMemberLinearLayout = null;
    private LinearLayout memberDetailsLinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_in_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        memberNameEditText = (EditText) findViewById(R.id.memberName_editText);
        newMemberLinearLayout = (LinearLayout) findViewById(R.id.newMember_linearLayout);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

        addNewMemberInGroupButton = (Button) findViewById(R.id.addNewMemberInGroup_button);
        addNewMemberInGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*
                //Verification du contenu des noms des membres
                for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++)
                    if (newMemberLinearLayout.getChildAt(i) instanceof EditText) {
                        EditText myEditText = (EditText) newMemberLinearLayout.getChildAt(i);
                        if (myEditText.getText().toString().matches(""))
                        {
                            Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    */

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
                            {
                                Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            editTextField++;
                        }
                    }
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

                groupXMLManipulator = new XMLManipulator(getApplicationContext());
                for (int i = 0 ; i < memberNameList.size() ; i++)
                {
                    groupXMLManipulator.addNewMemberInGroup(groupName, memberNameList.get(i));
                }

                //Retour a la fenetre du group
                Intent intent = new Intent(AddNewMemberInGroupActivity.this, GroupActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });

        addNewMemberRepertory = (Button) findViewById(R.id.add_repertory_name);
        addNewMemberRepertory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);//Contacts.People.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
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
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    ContentResolver cr = getContentResolver();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                   /* if (c.moveToFirst()) {
                        String id =
                                c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));//Contacts.People.NAME));
                        memberNameEditText.setText(id);
                    }*/

                    while (c.moveToNext()) {
                        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                memberNameEditText.setText(name + "   " + phone);
                            }
                        }
                    }
                }
                break;
        }
    }
}
