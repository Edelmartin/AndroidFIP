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
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
        memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);

        //Récupération de la list des transactions du group
        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        transactionList = groupXMLManipulator.getListTransactionOfGroup(groupName);
        transactionNameList.clear();
        for (Transaction transaction: transactionList)
            transactionNameList.add(transaction.getName());

        lv = (ListView) findViewById(R.id.memberName_listView);
        StringAdapter adapter = new StringAdapter(GroupActivity.this, memberNameList, mTouchListener);
        lv.setAdapter(adapter);

        lv1 = (ListView) findViewById(R.id.transactionName_listView);
        StringAdapter adapter1 = new StringAdapter(GroupActivity.this, transactionNameList,yTouchListener);
        lv1.setAdapter(adapter1);
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
                        .setMessage("Voulez-vous vraiment supprimer le groupe \"" + groupName + "\" ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                XMLManipulator groupXMLManipulator = new XMLManipulator(getApplicationContext());
                                groupXMLManipulator.deleteGroup(groupName);
                                Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
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
                memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);
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
            finish();
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
                memberNameList = groupXMLManipulator.getListMemberOfGroup(groupName);
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


    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0)
            {
                mSwipeSlop = ViewConfiguration.get(GroupActivity.this).getScaledTouchSlop();
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

                       /* if (deltaX > v.getWidth() / 3) // swipe to right
                        {
                            mDownX = x;
                            swiped = true;
                            mSwiping = false;
                            mItemPressed = false;


                            v.animate().setDuration(300).translationX(v.getWidth()/3); // could pause here if you want, same way as delete
                            TextView tv = (TextView) v.findViewById(R.id.list_tv);
                            tv.setText("Swiped!");
                            return true;
                        }
                        else*/ if ((deltaX < -1 * (v.getWidth() / 3)) || ((deltaX > v.getWidth() / 3)))// swipe to left
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
                                        AlertDialog.Builder demandefermeture = new AlertDialog.Builder(GroupActivity.this);

                                        int i = lv.getPositionForView(v);

                                        final String nomDuMembre = memberNameList.get(i).toString();
                                        //final String nomTransaction = (transactionNameList.get(i).toString());
                                        demandefermeture.setTitle("Suppression du membre");
                                        // set dialog message
                                        demandefermeture
                                                .setMessage("Voulez-vous vraiment supprimer le membre \"" + nomDuMembre + "\" ?")
                                                .setCancelable(false)
                                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // if this button is clicked, just close
                                                        // the dialog box and do nothing

                                                        //Suppression des Transaction du Member
                                                        ArrayList<Transaction> transactionListOfMember = new ArrayList<Transaction>();
                                                        transactionListOfMember = groupXMLManipulator.getListTransactionOfMember(groupName, nomDuMembre);
                                                        for (Transaction transaction: transactionListOfMember)
                                                        {
                                                            groupXMLManipulator.deleteTransaction(groupName, transaction.getName().toString());
                                                        }

                                                        XMLManipulator groupXMLManipulator = new XMLManipulator(getApplicationContext());
                                                        groupXMLManipulator.deleteMemberOfGroup(groupName,nomDuMembre);
                                                        mSwiping = false;
                                                        mItemPressed = false;
                                                        animateRemoval(lv, v);
                                                        //finish();
                                                        //startActivity(getIntent());
                                                        Intent intent = getIntent();
                                                        overridePendingTransition(0, 0);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                        startActivity(intent);
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

                        //Toast.makeText(GroupActivity.this, memberNameList.get(i).toString(), Toast.LENGTH_LONG).show();
                        String nomDuMembre = memberNameList.get(i).toString();
                        Intent k = new Intent(GroupActivity.this, EditMemberActivity.class);
                        k.putExtra("memberName", nomDuMembre);
                        k.putExtra("groupName", groupName);
                        startActivity(k);
                        return false;
                    }
                }
                default:
                    return false;
            }
            return true;
        }
    };

    private View.OnTouchListener yTouchListener = new View.OnTouchListener()
    {
        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0)
            {
                mSwipeSlop = ViewConfiguration.get(GroupActivity.this).getScaledTouchSlop();
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
                            lv1.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                    {
                        v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                        if ((deltaX < -1 * (v.getWidth() / 3)) || ((deltaX > v.getWidth() / 3)))// swipe to left
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
                                            AlertDialog.Builder demandefermeture = new AlertDialog.Builder(GroupActivity.this);

                                            int i = lv1.getPositionForView(v);

                                            final String nomTransaction = (transactionNameList.get(i).toString());
                                            demandefermeture.setTitle("Suppression d'une transaction");
                                            // set dialog message
                                            demandefermeture
                                                    .setMessage("Voulez-vous vraiment supprimer la transaction \"" + nomTransaction+ "\" ?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // if this button is clicked, just close
                                                            // the dialog box and do nothing
                                                            XMLManipulator groupXMLManipulator = new XMLManipulator(getApplicationContext());
                                                            groupXMLManipulator.deleteTransaction(groupName, nomTransaction);
                                                            mSwiping = false;
                                                            mItemPressed = false;
                                                            animateRemoval1(lv1, v);
                                                        }
                                                    })
                                                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // if this button is clicked, just close
                                                            // the dialog box and do nothing
                                                            dialog.cancel();
                                                            //finish();
                                                            startActivity(getIntent());
                                                            Intent intent = getIntent();
                                                            overridePendingTransition(0, 0);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                            startActivity(intent);
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
                                lv1.setEnabled(true);
                            }
                        });
                    }
                    else // user was not swiping; registers as a click
                    {
                        mItemPressed = false;
                        lv1.setEnabled(true);

                        int i = lv1.getPositionForView(v);

                        //Toast.makeText(GroupActivity.this, transactionNameList.get(i).toString(), Toast.LENGTH_LONG).show();
                        String nomTransaction = (transactionNameList.get(i).toString());
                        Intent u = new Intent(GroupActivity.this, EditTransactionActivity.class);
                        u.putExtra("transactionName", nomTransaction);
                        u.putExtra("groupName", groupName);
                        startActivity(u);
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

    // animates the removal of the view, also animates the rest of the view into position
    private void animateRemoval1(final ListView listView, View viewToRemove)
    {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        final ArrayAdapter adapter = (ArrayAdapter)lv1.getAdapter();
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
                                        lv1.setEnabled(true);
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
                                    lv1.setEnabled(true);
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


