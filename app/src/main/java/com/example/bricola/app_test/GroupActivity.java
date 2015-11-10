package com.example.bricola.app_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupActivity extends AppCompatActivity {

    private static String groupName = null;
    private static ArrayList<String> memberNameList = new ArrayList<String> ();
    private static ArrayList<Transaction> transactionList = new ArrayList<Transaction> ();
    private static ArrayList<String> transactionNameList = new ArrayList<String>();
    private static ListView memberNameListView = null;
    private static ListView transactionNameListView = null;
    private static XMLManipulator groupXMLManipulator;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

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

        if (getIntent().hasExtra("newTransactionName")) {
            String newTransactionName = extras.getString("newTransactionName");
            String newTransactionOwner = extras.getString("newTransactionOwner");
            Double newTransactionValue = Double.parseDouble(extras.getString("newTransactionValue"));
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date newTransactionDate = null;
            try {
                newTransactionDate = df.parse(extras.getString("newTransactionDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction newTransaction = new Transaction(newTransactionName, newTransactionOwner, newTransactionValue, newTransactionDate);
            groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
            groupXMLManipulator.addNewTransaction(groupName, newTransaction);
        }

        //Récupération de la list des member du group
        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        try {
            memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        //Récupération de la list des transactions du group
        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        transactionList = groupXMLManipulator.getListTransactionOfGroup(groupName);
        transactionNameList.clear();
        for (Transaction transaction: transactionList)
            transactionNameList.add(transaction.getName());

        memberNameListView = (ListView) findViewById(R.id.memberName_listView);
        ArrayAdapter<String> memberArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memberNameList);
        memberNameListView.setAdapter(memberArrayAdapter);

        transactionNameListView = (ListView) findViewById(R.id.transactionName_listView);
        ArrayAdapter<String> transactionArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, transactionNameList);
        transactionNameListView.setAdapter(transactionArrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_addTransaction:
                intent = new Intent(GroupActivity.this, AddNewTransactionActivity.class);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                break;
            case R.id.action_addMember:
                intent = new Intent(GroupActivity.this, AddNewMemberInGroupActivity.class);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                break;
            case R.id.action_deleteTransaction:
                intent = new Intent(GroupActivity.this, DeleteTransactionActivity.class);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                break;
            case R.id.action_deleteMember:
                intent = new Intent(GroupActivity.this, DeleteMemberInGroupActivity.class);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                break;
            case R.id.action_deleteGroup:

                AlertDialog.Builder demandefermeture = new AlertDialog.Builder(GroupActivity.this);

                demandefermeture.setTitle("Suppression du groupe");
                // set dialog message
                demandefermeture
                        .setMessage("Click Yes to delete")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                XMLManipulator groupXMLManipulator = new XMLManipulator(getApplicationContext());
                                groupXMLManipulator.deleteGroup(groupName);
                                Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = demandefermeture.create();
                alertDialog.show();
                break;
            case R.id.action_balanceAccount:
                //Détermination des transaction de remboursement à faire
                ArrayList<RefundTransaction> refundTransactionList = new ArrayList<RefundTransaction>();
                ArrayList<Member> memberList = new ArrayList<Member>();
                ArrayList<String> memberNameList = new ArrayList<String>();
                groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
                try {
                    memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
                for (String memberName : memberNameList)
                {
                    Double totalTransactionAmount = groupXMLManipulator.getTotalTransactionAmountOfMember(groupName, memberName);
                    try {
                        memberList.add(new Member(memberName, totalTransactionAmount));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Calculator calculator = new Calculator();
                try {
                    refundTransactionList = calculator.calculateRefundTransactions(memberList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Création de la nouvelle activité avec les extra contenant les transactions de remboursement à faire
                intent = new Intent(GroupActivity.this, BalanceAccountActivity.class);
                ArrayList<String> refundReceiverName = new ArrayList<String>();
                ArrayList<String> refundDonatorName = new ArrayList<String>();
                ArrayList<String> refundValue = new ArrayList<String>();
                for (RefundTransaction refundTransaction: refundTransactionList)
                {
                    refundReceiverName.add(refundTransaction.getReceiverName());
                    refundDonatorName.add(refundTransaction.getDonatorName());
                    refundValue.add(refundTransaction.getValue().toString());
                }
                intent.putExtra("groupName", groupName);
                intent.putStringArrayListExtra("receiverNames", refundReceiverName);
                intent.putStringArrayListExtra("donatorNames", refundDonatorName);
                intent.putStringArrayListExtra("values", refundValue);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(GroupActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //called method for shake event
    private void handleShakeEvent(int count) {
        MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.money);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer arg0) {
                Intent intent = null;
                //Détermination des transaction de remboursement à faire
                ArrayList<RefundTransaction> refundTransactionList = new ArrayList<RefundTransaction>();
                ArrayList<Member> memberList = new ArrayList<Member>();
                ArrayList<String> memberNameList = new ArrayList<String>();
                groupXMLManipulator = new XMLManipulator(getApplicationContext());
                try {
                    memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
                for (String memberName : memberNameList)
                {
                    Double totalTransactionAmount = groupXMLManipulator.getTotalTransactionAmountOfMember(groupName, memberName);
                    try {
                        memberList.add(new Member(memberName, totalTransactionAmount));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Calculator calculator = new Calculator();
                try {
                    refundTransactionList = calculator.calculateRefundTransactions(memberList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Création de la nouvelle activité avec les extra contenant les transactions de remboursement à faire
                intent = new Intent(GroupActivity.this, BalanceAccountActivity.class);
                ArrayList<String> refundReceiverName = new ArrayList<String>();
                ArrayList<String> refundDonatorName = new ArrayList<String>();
                ArrayList<String> refundValue = new ArrayList<String>();
                for (RefundTransaction refundTransaction: refundTransactionList)
                {
                    refundReceiverName.add(refundTransaction.getReceiverName());
                    refundDonatorName.add(refundTransaction.getDonatorName());
                    refundValue.add(refundTransaction.getValue().toString());
                }
                intent.putExtra("groupName", groupName);
                intent.putStringArrayListExtra("receiverNames", refundReceiverName);
                intent.putStringArrayListExtra("donatorNames", refundDonatorName);
                intent.putStringArrayListExtra("values", refundValue);
                startActivity(intent);
            }

        });
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
