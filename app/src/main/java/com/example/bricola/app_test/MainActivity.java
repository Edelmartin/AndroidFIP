package com.example.bricola.app_test;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    //test

    private static ArrayList<String> groupNameList = new ArrayList<String> ();
    private String groupXMLFile = "group.xml";
    private ListView groupNameListView = null;
    private XMLManipulator groupXMLManipulator;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }
        });
        this.setTitle("Partage de frais");

       //Création du fichier group.xml si le fichier n'existe pas
        File groupXMLFilePath = new File (this.getFilesDir().toString() + "/" + groupXMLFile);
        if (!groupXMLFilePath.exists())
        {
            try {
                groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
                groupXMLManipulator.createEmptyGroupXMLFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                if (getIntent().hasExtra("groupName"))
                    try {
                        ArrayList<String> NewGroupMemberNameList = new ArrayList<String>();
                        NewGroupMemberNameList = extras.getStringArrayList("memberList");
                        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
                        groupXMLManipulator.addNewGroupWithMember(extras.getString("groupName"), NewGroupMemberNameList);
                    } catch (IOException | XmlPullParserException e) {
                        e.printStackTrace();
                    }
            }
        }
        //Lecture des groupes du fichier group.xml
        try {
            groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
            groupNameList = groupXMLManipulator.getListGroup();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        //Affichage des groupes
        groupNameListView = (ListView) findViewById(R.id.groupName_listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupNameList);
        groupNameListView.setAdapter(arrayAdapter);
        //Affichage du contenu d'un groupe après sélection par clic
        groupNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                String groupName = (String) parent.getItemAtPosition(position);
                intent.putExtra("groupName" , groupName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addGroup) {
            Intent intent = new Intent(MainActivity.this, AddGroupActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    //called method for shake event
    private void handleShakeEvent(int count) {
        Intent intent = new Intent(MainActivity.this, AddGroupActivity.class);
        startActivity(intent);
    }
    @Override

    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
