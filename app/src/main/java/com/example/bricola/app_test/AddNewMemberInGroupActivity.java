package com.example.bricola.app_test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewMemberInGroupActivity extends AppCompatActivity {

    public static final int PICK_CONTACT = 1;

    private Button addNewMemberButton = null;
    private Button addNewMemberRepertory = null;
    private Button deleteMemberButton = null;
    private EditText memberNameEditText = null;
    private EditText memberNumberEditText = null;
    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private LinearLayout newMemberLinearLayout = null;
    private LinearLayout memberDetailsLinearLayout = null;

    ArrayList<String> memberNameList1 = new ArrayList<String>();
    ArrayList<String> memberNumberList1 = new ArrayList<String>();
    ArrayList<Bitmap> memberphoto = new ArrayList<>();

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

        memberNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                memberNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        memberNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                memberNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);//TYPE_CLASS_PHONE);
                v.setFocusableInTouchMode(true);
                return false;
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
                if (newMemberLinearLayout.getChildCount() == 1) {
                    deleteMemberButton.setEnabled(true);
                }
                if (newMemberLinearLayout.getChildCount() >= 1) {
                    LinearLayout newMemberDetailsLinearLayout = new LinearLayout(getApplication());
                    newMemberDetailsLinearLayout.setOrientation(LinearLayout.VERTICAL);

                    TextView newMemberNameTextView = new TextView(getApplication());
                    newMemberNameTextView.setText("Nom:");
                    newMemberNameTextView.setTextColor(Color.parseColor("#7e7e7e"));
                    newMemberDetailsLinearLayout.addView(newMemberNameTextView);

                    EditText newMemberNameEditText = (EditText) getLayoutInflater().inflate(R.layout.newedittextstyle, null);
                    newMemberNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    newMemberDetailsLinearLayout.addView(newMemberNameEditText);

                    int pixelsValue = 50; // margin in pixels
                    float d = getApplicationContext().getResources().getDisplayMetrics().density;
                    int margin = (int)(pixelsValue * d);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(margin, margin);
                    layoutParams.gravity = Gravity.RIGHT;
                    ImageView newImageContact = new ImageView(getApplication());
                    newImageContact.setImageResource(R.mipmap.no_contact);
                    newImageContact.setLayoutParams(layoutParams);
                    newMemberDetailsLinearLayout.addView(newImageContact);

                    TextView newMemberNumberTextView = new TextView(getApplication());
                    newMemberNumberTextView.setText("Mail ou numéro de téléphone:");
                    newMemberNumberTextView.setTextColor(Color.parseColor("#7e7e7e"));
                    newMemberDetailsLinearLayout.addView(newMemberNumberTextView);


                    EditText newMemberNumberEditText = (EditText) getLayoutInflater().inflate(R.layout.newedittextstyle, null);
                    newMemberNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);//TYPE_CLASS_PHONE);
                    newMemberDetailsLinearLayout.addView(newMemberNumberEditText);

                    newMemberLinearLayout.addView(newMemberDetailsLinearLayout);
                }
            }
        });

        deleteMemberButton = (Button) findViewById(R.id.deleteMember_button);
        deleteMemberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (newMemberLinearLayout.getChildCount() == 2) {
                    deleteMemberButton.setEnabled(false);
                }
                if (newMemberLinearLayout.getChildCount() > 1) {
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

        ArrayList<String> s = new ArrayList<String>();
        ArrayList<String> number_reception = new ArrayList<String>();
        ArrayList<String> mail_reception = new ArrayList<String>();

        Integer mail_ou_telephone = 0;

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

                        monLong_id = Long.parseLong(id);

                        if (picture != null) {
                            monlong_picture_id = Long.parseLong(picture);
                        } else {
                            monlong_picture_id = Long.parseLong("0");
                        }

                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                mail_ou_telephone = 1;
                                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String type = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                s.add((String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), Integer.parseInt(type), ""));

                                //Replace +33 par 0
                                phone = phone.replaceAll("\\+33", "0");
                                //Remove all non digit characters
                                phone = phone.replaceAll("\\D+", "");

                                name_of_contacts = name;
                                number_reception.add(phone);
                            }
                            pCur.close();
                            // photo = loadContactPhoto(photo_appli, monLong_id, monlong_picture_id);//301,2158);
                            //ouverture_alertdialog(s, number_reception, mail_ou_telephone, name_of_contacts, photo,monlong_picture_id);
                        } else {
                            mail_ou_telephone = 2;
                            Cursor pCurmail = crmail.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCurmail.moveToNext()) {
                                String mail = pCurmail.getString(pCurmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                int type = pCurmail.getInt(pCurmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                                s.add((String) ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), type, ""));
                                name_of_contacts = name;
                                number_reception.add(mail);
                            }
                            pCurmail.close();
                            // photo = loadContactPhoto(photo_appli, monLong_id, monlong_picture_id);//301,2158);
                            //ouverture_alertdialog(s, number_reception, mail_ou_telephone, name_of_contacts, photo,monlong_picture_id);
                        }
                        photo = loadContactPhoto(photo_appli, monLong_id, monlong_picture_id);//301,2158);
                        ouverture_alertdialog(s, number_reception, mail_ou_telephone, name_of_contacts, photo, monlong_picture_id);
                    }
                    //memberNameEditText.setText(name_of_contacts);
                }
                break;
        }
    }

    public Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null) {
            return BitmapFactory.decodeStream(input);
        } else {
            Log.d("PHOTO", "first try failed to load photo");
        }
        byte[] photoBytes = null;

        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        } finally {
            c.close();
        }
        if (photoBytes != null)
            return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }

    public void ouverture_alertdialog(ArrayList<String> type, final ArrayList<String> numero_portable, Integer mail_ou_phone, final String contact_name, final Bitmap photo_contact, final long id_de_la_photo) {
        final int numero_choisi;
        ArrayList<String> description_contact = new ArrayList<String>();
        for (int i = 0; i < type.size(); i++) {
            description_contact.add(type.get(i) + "\n" + numero_portable.get(i));
        }

        final CharSequence myList[] = description_contact.toArray(new CharSequence[description_contact.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewMemberInGroupActivity.this);

        if (mail_ou_phone == 1) {
            // Set the dialog title
            builder.setTitle("Choix du numéro");
        } else if (mail_ou_phone == 2) {
            builder.setTitle("Choix de l'adresse mail");
        }
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
        builder
                .setSingleChoiceItems(myList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getApplicationContext(), "You Choose : " + myList[arg1], Toast.LENGTH_LONG).show();
                    }
                })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition(); //récupère le choix de alert view (plusieurs numéro)
                        // memberNumberEditText.setText(numero_portable.get(selectedPosition));
                        remplissage_ajout_repertoire(numero_portable.get(selectedPosition), contact_name, photo_contact, id_de_la_photo);
                        //imageviewcontact = (ImageView) findViewById(R.id.img_contacts);
                        // imageviewcontact.setImageBitmap(photo_contact);
                        //AddNewMemberInGroupActivity.this.finishActivity(PICK_CONTACT);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

       // alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            //@Override
           // public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
             //   if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //Toast.makeText(getApplicationContext(), "You Choose : Close Alert Dialog ", Toast.LENGTH_LONG).show();
              //  }
               // return true;
           // }
       // });
        //onKeyDown()
    }

    public void remplissage_ajout_repertoire(String str, String contactName, Bitmap member_contact_photo, long id_de_la_photo) {
        Boolean premier_linear_vide = false;

        if (memberNameList1.contains(contactName)) {
            contactName = "";
            str = "";
            id_de_la_photo = 0;
            memberNumberList1.add(str);
            memberNameList1.add(contactName);
            Toast toast = Toast.makeText(getApplicationContext(), "Contact déjà sélectionné ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(18);
            toast.show();
        }
       /* else if (memberNumberList1.contains(str))
        {
            contactName = "";
            str = "";
            memberNumberList1.add(str);
            memberNameList1.add(contactName);
            Toast toast = Toast.makeText(getApplicationContext(), "Deux contact ont le même numéro " , Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(15);
            toast.show();
        }*/
        else {
            memberNumberList1.add(str);
            memberNameList1.add(contactName);
        }

        for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++) {
            Integer editTextField = 0;
            memberDetailsLinearLayout = (LinearLayout) newMemberLinearLayout.getChildAt(i);
            if (premier_linear_vide == false) {
                for (int j = 0; j < memberDetailsLinearLayout.getChildCount(); j++) {
                    if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 0)) {
                        String test1 = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                        if (test1.equals("")) {
                            ((EditText) memberDetailsLinearLayout.getChildAt(j)).setText(contactName);
                            premier_linear_vide = true;
                            if (id_de_la_photo != 0) {
                                ((ImageView) memberDetailsLinearLayout.getChildAt(j + 1)).setImageBitmap(member_contact_photo);
                            } else {
                                ((ImageView) memberDetailsLinearLayout.getChildAt(j + 1)).setImageResource(R.mipmap.no_contact);
                            }
                        }
                        editTextField++;
                    } else if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 1)) {
                        String test2 = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                        if (test2.equals(""))
                            ((EditText) memberDetailsLinearLayout.getChildAt(j)).setText(str);
                    }
                }
            }
        }
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
            addMemberInGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMemberInGroup () {
        //Verification du contenu des noms des membres pour ne pas qu'ils soient vides
        for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++) {
            Integer editTextField = 0;
            memberDetailsLinearLayout = (LinearLayout) newMemberLinearLayout.getChildAt(i);
            for (int j = 0; j < memberDetailsLinearLayout.getChildCount(); j++) {
                if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 0)) {
                    String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                    if (str.equals("")) {
                        Toast.makeText(getApplication(), "Vous n'avez pas complété le nom d'un membre", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editTextField++;
                } else if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 1)) {
                    String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                    if (!(isANumber(str) || isAMail(str) || str.equals(""))) {
                        Toast.makeText(getApplication(), "Vous avez mal complété le contact d'un membre (numéro ou mail)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }

        //Récupération du nom et du numero des membres
        ArrayList<String> memberNameList = new ArrayList<String>();
        ArrayList<String> memberContactList = new ArrayList<String>();
        for (int i = 0; i < newMemberLinearLayout.getChildCount(); i++) {
            Integer editTextField = 0;
            memberDetailsLinearLayout = (LinearLayout) newMemberLinearLayout.getChildAt(i);
            for (int j = 0; j < memberDetailsLinearLayout.getChildCount(); j++) {
                if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 0)) {
                    String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                    memberNameList.add(str);
                    editTextField++;
                } else if ((memberDetailsLinearLayout.getChildAt(j) instanceof EditText) && (editTextField == 1)) {
                    String str = ((EditText) memberDetailsLinearLayout.getChildAt(j)).getText().toString();
                    memberContactList.add(str);
                }
            }
        }

        groupXMLManipulator = new XMLManipulator(getApplicationContext());

        ArrayList<String> memberXMLNameList = new ArrayList<String>();
        memberXMLNameList = groupXMLManipulator.getListMemberOfGroup(groupName);

        ArrayList<String> tutu = new ArrayList<String>();

        for (int j = 0; j < memberXMLNameList.size(); j++) {
            for (int i = 0; i < memberNameList.size(); i++) {
                if ((memberXMLNameList.get(j).toString().toUpperCase().equals(memberNameList.get(i).toString().toUpperCase()))) {
                    Toast.makeText(getApplication(), memberNameList.get(i).toString() + " est déjà membre du groupe", Toast.LENGTH_SHORT).show();
                    tutu.add(memberNameList.get(i));
                }
            }
        }

        //int value ;Integer.parseInt(string);
        for (int k = 0; k < tutu.size(); k++)
        {
            memberNameList.remove(tutu.get(k));
        }

        for (int i = 0; i < memberNameList.size(); i++) {
            groupXMLManipulator.addNewMemberInGroup(groupName, memberNameList.get(i), memberContactList.get(i));
        }

        //Retour a la fenetre du group
        Intent intent = new Intent(AddNewMemberInGroupActivity.this, GroupActivity.class);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
        finish();

    }

    Boolean isANumber (String str)
    {     if(str.length() != 10)
        //if (str.length() < 10 && str.length() > 13)
            return false;
        Boolean isOk = true;
        for (int i = 0 ; i < 10 ; i++)
        {
            char c = str.charAt(i);
            if (!(c >= '0' && c <= '9'))
                isOk = false;
        }
        return isOk;
    }

    Boolean isAMail (String str)
    {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewMemberInGroupActivity.this, GroupActivity.class);
        intent.putExtra("groupName" , groupName);
        startActivity(intent);
        finish();
    }

}

