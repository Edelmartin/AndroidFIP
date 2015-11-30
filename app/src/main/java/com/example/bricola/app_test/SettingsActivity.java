package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static Button emptyGroupXmlButton = null;
    private static XMLManipulator groupXMLManipulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Options");

        emptyGroupXmlButton = (Button) findViewById(R.id.emptyGroupXml_button);
        emptyGroupXmlButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                groupXMLManipulator = new XMLManipulator(getApplicationContext());
                try {
                    groupXMLManipulator.createEmptyGroupXMLFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
