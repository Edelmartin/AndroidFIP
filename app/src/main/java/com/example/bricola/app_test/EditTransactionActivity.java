package com.example.bricola.app_test;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditTransactionActivity extends AppCompatActivity {
    private static String  transactionName,groupName;
    private static EditText transactionValueView = null;
    private static Spinner transactionOwnerSpinner=null;
    private static EditText transactionNameView = null;
    private static ArrayList<String> listOfMember = new ArrayList<String>();
    private static ArrayList<Transaction> transactionList = new ArrayList<Transaction> ();
    private DatePicker transactionDateDatePicker = null;
    private static int position;
    private static Transaction initial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        transactionName = extras.getString("transactionName");
        groupName = extras.getString("groupName");

        this.setTitle("Transaction");

        transactionNameView = (EditText) findViewById(R.id.TrasactionName_editText);
        transactionOwnerSpinner = (Spinner) findViewById(R.id.transactionOwner_spinner);
        transactionValueView = (EditText) findViewById(R.id.TransactionValue_editText);
        transactionDateDatePicker = (DatePicker) findViewById(R.id.transaction_datePicker);

        //Récupération de la liste des numéros
        XMLManipulator xmlmanip = new XMLManipulator(EditTransactionActivity.this);
        ArrayList<Transaction> listTransaction = xmlmanip.getListTransactionOfGroup(groupName);

        //initialise le champ nom de la transaction

        transactionNameView.setText(transactionName);

        // test tous les "contacts" du groupe
        for (int i=0 ; i < listTransaction.size() ; i++) {

            //si le contact correspond a celui sur lequel on a cliqué
            if (listTransaction.get(i).getName().matches(transactionName)) {
                initial=listTransaction.get(i);//on initialise les autres edit text
                initial.setOwner(listTransaction.get(i).getOwner().toString());
                listOfMember = xmlmanip.getListMemberOfGroup(groupName);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, listOfMember);
                transactionOwnerSpinner.setAdapter(adapter);
                for(i=0; i < adapter.getCount(); i++){
                    if(adapter.getItem(i).matches(initial.getOwner().toString())) {
                        position = i;
                        transactionOwnerSpinner.setSelection(position);
                    }
                }
                transactionValueView.setText(initial.getValue().toString());

                Date date1 = null;
                Calendar tdy1 = null;
                date1=initial.getDate();
                tdy1 = DateToCalendar(date1);
                int xxday = tdy1.get(Calendar.DATE);
                int xxmonth = tdy1.get(Calendar.MONTH);
                int xxyear = tdy1.get(Calendar.YEAR);

                transactionDateDatePicker.init(xxyear, xxmonth, xxday, null);

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
            modifyTransaction();
        }
        return super.onOptionsItemSelected(item);
    }

    public void modifyTransaction ()
    {
        Integer day = transactionDateDatePicker.getDayOfMonth();
        Integer month = transactionDateDatePicker.getMonth() + 1;
        Integer year = transactionDateDatePicker.getYear();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date transactionDate = null;
        try {
            transactionDate = df.parse(new String(checkDigit(day).toString() + "/" + checkDigit(month).toString() + "/" + year.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Verification du contenu des EditText qui ne doivent pas être vides
        if( !transactionNameView.getText().toString().matches("") && !transactionValueView.getText().toString().matches("")  ) {
            //si l'un des 2 champs a été modifié
            if (!transactionNameView.getText().toString().matches(initial.getName().toString()) || !transactionValueView.getText().toString().matches(initial.getValue().toString())
                    || !transactionOwnerSpinner.getSelectedItem().toString().matches(initial.getOwner().toString()) || !transactionDate.toString().matches(initial.getDate().toString())) {

                XMLManipulator xmlmanip = new XMLManipulator(EditTransactionActivity.this);
                Double transactionValue = Double.parseDouble(transactionValueView.getText().toString());

                //si le nom du membre a changé
                if (!transactionNameView.getText().toString().matches(initial.getName().toString())) {
                    xmlmanip.modifyTransactionName(groupName, initial.getName().toString(), transactionNameView.getText().toString());
                    //il faut réaffcecter le nom correct a toutes les transactions
                }
                //si le montant a changé
                if (!transactionValueView.getText().toString().matches(initial.getValue().toString())) {
                    xmlmanip. modifyTransactionValue(groupName,transactionNameView.getText().toString(),transactionValue);
                }
                //si le owner a changé
                if(!transactionOwnerSpinner.getSelectedItem().toString().matches(initial.getOwner().toString())){
                    xmlmanip.modifyTransactionOwner(groupName,transactionNameView.getText().toString(),transactionOwnerSpinner.getSelectedItem().toString());
                }
                //si la date a changé
                if(!transactionDate.toString().matches(initial.getDate().toString())){
                    Transaction newTransaction = new Transaction(transactionName, transactionOwnerSpinner.getSelectedItem().toString(), transactionValue, transactionDate);
                    xmlmanip.modifyTransaction(groupName, transactionName,newTransaction);
                }

                Toast.makeText(getApplication(), "Transaction modifiée", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplication(), "Aucune modification apportée", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplication(), "Vous avez mal complété une zone de texte", Toast.LENGTH_SHORT).show();
            return;
        }
        //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
        Intent intent = new Intent(EditTransactionActivity.this, GroupActivity.class);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    public static Calendar DateToCalendar(Date date )
    {
        Calendar cal = null;
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String d1 = formatter.format(date);
            date = (Date)formatter.parse(d1);
            cal= Calendar.getInstance();
            cal.setTime(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return cal;
    }

    public String checkDigit(int number)
{
        return number<=9?"0"+number:String.valueOf(number);
        }

        }
