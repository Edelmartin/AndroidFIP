package com.example.bricola.app_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.HashMap;

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
    private MediaPlayer player;

    //Swiping
    private ListView lv;
    private ListView lv1;
    private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
    private boolean mItemPressed = false; // Detects if user is currently holding down a view
    private static final int SWIPE_DURATION = 250; // needed for velocity implementation
    private static final int MOVE_DURATION = 150;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //Lecture des groupes du fichier group.xml
        try {
            groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
            groupNameList = groupXMLManipulator.getListGroup();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        /*//Affichage des groupes
        groupNameListView = (ListView) findViewById(R.id.groupName_listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupNameList);
        groupNameListView.setAdapter(arrayAdapter);
        //Affichage du contenu d'un groupe après sélection par clic
        groupNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                intent.addFlags(//Intent.FLAG_ACTIVITY_NEW_TASK
                       // | Intent.FLAG_ACTIVITY_CLEAR_TOP
                         Intent.FLAG_ACTIVITY_CLEAR_TASK);
                String groupName = (String) parent.getItemAtPosition(position);
                intent.putExtra("groupName" , groupName);
                startActivity(intent);
                finish();
            }
        });*/

        Collections.sort(groupNameList, String.CASE_INSENSITIVE_ORDER);

        lv = (ListView) findViewById(R.id.groupName_listView);
        StringAdapter adapter = new StringAdapter(MainActivity.this, groupNameList, mTouchListener);
        lv.setAdapter(adapter);
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
            finish();
        }
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //TextView title = new TextView(this);
        //title.setText("Quitter l'application");
        //title.setGravity(Gravity.CENTER);
        //title.setTextColor(0xFF00FF00);
        //title.setTextSize(23);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quitter l'application");
        builder.setMessage("Voulez-vous vraiment quitter l'application ?");
        builder.setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("Annuler", null);
                AlertDialog dialog = builder.show();

        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;
        Integer counter_member = 0;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0)
            {
                mSwipeSlop = ViewConfiguration.get(MainActivity.this).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed)
                    {
                        // Doesn't allow swiping two items at same time
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    swiped = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);

                    if (!mSwiping)
                    {
                        if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
                        {
                            mSwiping = true;
                            lv.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                    {
                        v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped



                        if (deltaX > v.getWidth() / 3) // swipe to right
                        {
                            if (counter_member == 0) {
                                mItemPressed = false;
                                lv.setEnabled(true);

                                int i = lv.getPositionForView(v);

                                Intent intent = new Intent(MainActivity.this, edit_groupe_name.class);
                                intent.addFlags(//Intent.FLAG_ACTIVITY_NEW_TASK
                                        // | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                String nomDuGroupe = groupNameList.get(i).toString();
                                intent.putExtra("groupName", nomDuGroupe);
                                startActivity(intent);
                                counter_member=1;
                                finish();
                                return false;
                            }
                        }
                        else if ((deltaX < -1 * (v.getWidth() / 3)) )// swipe to left
                        {

                            v.setEnabled(false); // need to disable the view for the animation to run

                            // stacked the animations to have the pause before the views flings off screen
                            v.animate().setDuration(300).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
                                @Override
                                public void run()
                                {
                                    v.animate().setDuration(300).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            AlertDialog.Builder demandefermeture = new AlertDialog.Builder(MainActivity.this);

                                            int i = lv.getPositionForView(v);

                                            final String nomDuGroupe_a_supprimer = groupNameList.get(i).toString();
                                            //final String nomTransaction = (transactionNameList.get(i).toString());
                                            demandefermeture.setTitle("Suppression d'un groupe");
                                            // set dialog message
                                            demandefermeture
                                                    .setMessage("Voulez-vous vraiment supprimer le groupe \"" + nomDuGroupe_a_supprimer + "\" ?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // if this button is clicked, just close
                                                            // the dialog box and do nothing

                                                            XMLManipulator groupXMLManipulator = new XMLManipulator(getApplicationContext());
                                                            groupXMLManipulator.deleteGroup(nomDuGroupe_a_supprimer);
                                                            mSwiping = false;
                                                            mItemPressed = false;
                                                            animateRemoval(lv, v);

                                                            startActivity(getIntent());
                                                            Intent intent = getIntent();
                                                            overridePendingTransition(0, 0);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                            //startActivity(intent);
                                                            finish();
                                                            //startActivity(getIntent());
                                                        }
                                                    })
                                                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // if this button is clicked, just close
                                                            // the dialog box and do nothing
                                                            //dialog.cancel();
                                                            //finish();
                                                            //startActivity(getIntent());
                                                            dialog.cancel();
                                                            Intent intent = getIntent();
                                                            overridePendingTransition(0, 0);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                            AlertDialog alertDialog = demandefermeture.create();
                                            alertDialog.show();
                                        }
                                    });
                                }
                            });
                            mDownX = x;
                            swiped = true;
                            return true;
                        }
                    }

                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    if (mSwiping) // if the user was swiping, don't go to the and just animate the view back into position
                    {
                        v.animate().setDuration(300).translationX(0).withEndAction(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mSwiping = false;
                                mItemPressed = false;
                                lv.setEnabled(true);
                            }
                        });
                    }
                    else // user was not swiping; registers as a click
                    {
                        mItemPressed = false;
                        lv.setEnabled(true);

                        int i = lv.getPositionForView(v);

                        Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                        intent.addFlags(//Intent.FLAG_ACTIVITY_NEW_TASK
                                // | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        String nomDuGroupe = groupNameList.get(i).toString();
                        intent.putExtra("groupName" , nomDuGroupe);
                        startActivity(intent);
                        finish();
                        return false;
                    }
                }
                default:
                    return false;
            }
            return true;
        }
    };

    // animates the removal of the view, also animates the rest of the view into position
    private void animateRemoval(final ListView listView, View viewToRemove)
    {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        final ArrayAdapter adapter = (ArrayAdapter)lv.getAdapter();
        for (int i = 0; i < listView.getChildCount(); ++i)
        {
            View child = listView.getChildAt(i);
            if (child != viewToRemove)
            {
                int position = firstVisiblePosition + i;
                long itemId = listView.getAdapter().getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        adapter.remove(adapter.getItem(listView.getPositionForView(viewToRemove)));

        final ViewTreeObserver observer = listView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                for (int i = 0; i < listView.getChildCount(); ++i) {
                    final View child = listView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        lv.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mSwiping = false;
                                    lv.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

}

