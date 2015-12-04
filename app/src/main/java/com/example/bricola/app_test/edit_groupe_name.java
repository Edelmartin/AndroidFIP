package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class edit_groupe_name extends AppCompatActivity {
    private static String groupName;
    private static EditText GroupeView = null;
    private static String initialGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_groupe_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");

        this.setTitle("Groupe");

        GroupeView = (EditText) findViewById(R.id.memberGroupe_editText);

        //initialise le chmap nom du groupe
        initialGroupName = groupName;
        GroupeView.setText(initialGroupName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saveChange) {
            modifyGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    public void modifyGroup ()
    {
        //Verification du contenu des EditText qui ne doivent pas être vides ou mal complétées
        if( !GroupeView.getText().toString().matches("")) {
                //si l'un des 2 champs a été modifié
                if (!GroupeView.getText().toString().matches(initialGroupName)) {

                    XMLManipulator xmlmanip = new XMLManipulator(edit_groupe_name.this);

                    //si le contact a changés
                    if (!GroupeView.getText().toString().matches(initialGroupName)) {
                        xmlmanip.modifyGroupName(initialGroupName,GroupeView.getText().toString());
                                //groupName, MemberView.getText().toString(), ContactView.getText().toString());
                    }
                    Toast.makeText(getApplication(), "Membre modifié", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplication(), "Aucune modification apportée", Toast.LENGTH_SHORT).show();
                }
        }
        else{
            Toast.makeText(getApplication(), "Vous n'avez pas complété le nom du membre", Toast.LENGTH_SHORT).show();
            return;
        }
        //Retour a la fenetre du group
        Intent intent = new Intent(edit_groupe_name.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(edit_groupe_name.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
