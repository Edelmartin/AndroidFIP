package com.example.bricola.app_test;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddNewMemberInGroupActivity extends AppCompatActivity {

    public static final int PICK_CONTACT    = 1;

    private Button addNewMemberInGroupButton = null;
    private Button addNewMemberButton = null;
    private Button addNewMemberRepertory = null;
    private Button deleteMemberButton = null;
    private EditText memberNameEditText = null;
    private EditText memberNumberEditText = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private LinearLayout newMemberLinearLayout = null;
    private LinearLayout memberDetailsLinearLayout = null;
    private ImageView imageviewcontact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_in_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        memberNameEditText = (EditText) findViewById(R.id.memberName_editText);
        newMemberLinearLayout = (LinearLayout) findViewById(R.id.newMember_linearLayout);

        memberNumberEditText = (EditText) findViewById(R.id.memberNumber_editText);

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
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);//Contacts.People.CONTENT_URI); // creates the contact list intent
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
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
        Bitmap photo = null;
        long monlong_picture_id = 0;
        long monLong_id = 0;
        String name_of_contacts = null;
        String number_or_mail = null;

        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData(); // has the uri for picked contact
                    //Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                    ContentResolver cr = getContentResolver();
                    ContentResolver photo_appli = getContentResolver();
                    ContentResolver crmail = getContentResolver();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null); // creates the contact cursor with the returned uri

                    while (c.moveToNext()) {
                        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String picture = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));

                        monLong_id=Long.parseLong(id);

                        if (picture!= null){
                            monlong_picture_id=Long.parseLong(picture);
                        }
                        else {
                            monlong_picture_id=Long.parseLong("0");
                        }
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);


                            while (pCur.moveToNext()) {
                                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                //Replace +33 par 0
                                phone = phone.replaceAll("\\+33","0");
                                //Remove all non digit characters
                                phone = phone.replaceAll("\\D+","");

                                name_of_contacts= name;
                                number_or_mail = phone;
                            }
                        }
                        else {
                            Cursor pCurmail = crmail.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id},null);
                            while (pCurmail.moveToNext()) {
                                String mail = pCurmail.getString(pCurmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                name_of_contacts=name;
                                number_or_mail=mail;
                            }
                        }
                    }
                        photo = loadContactPhoto(photo_appli, monLong_id, monlong_picture_id);//301,2158);
                        imageviewcontact = (ImageView) findViewById(R.id.img_contacts);
                        imageviewcontact.setImageBitmap(photo);
memberNameEditText.setText(name_of_contacts);
memberNumberEditText.setText(number_or_mail);
                }
                break;
        }
    }

    public  Bitmap loadContactPhoto(ContentResolver cr, long  id,long photo_id)
    {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null)
        {
            return BitmapFactory.decodeStream(input);
        }
        else
        {
            Log.d("PHOTO","first try failed to load photo");
        }
        byte[] photoBytes = null;

        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor c = cr.query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

        try
        {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        } finally {

            c.close();
        }
        if (photoBytes != null)
            return BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length);
        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }
    }
