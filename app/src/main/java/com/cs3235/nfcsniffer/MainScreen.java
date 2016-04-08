package com.cs3235.nfcsniffer;

/**
 * Created by mingxuan on 5/4/2016.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.triangle.Session;
import io.triangle.reader.PaymentCard;
import io.triangle.reader.ScanActivity;


public class MainScreen extends Activity {


    private boolean isResumed;
    private static final int SCAN_REQUEST_CODE = 100;
    private boolean hasRequestedScan;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanCard();
            }
        });

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        boolean askingToEnableNfc = false;

        if (nfcAdapter != null && !nfcAdapter.isEnabled())
        {
            askingToEnableNfc = true;

            // Alert the user that NFC is off
            new AlertDialog.Builder(this)
                    .setTitle("NFC Sensor Turned Off")
                    .setMessage("In order to use this application, the NFC sensor must be turned on. Do you wish to turn it on?")
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Send the user to the settings page and hope they turn it on
                            if (android.os.Build.VERSION.SDK_INT >= 16)
                            {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            }
                            else
                            {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    .setNegativeButton("Do Nothing", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Do nothing
                        }
                    })
                    .show();
        }

        t = (TextView)findViewById(R.id.counter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scanCard()
    {
        Intent scanIntent = new Intent(this, io.triangle.reader.ScanActivity.class);

        // We want the scanning to continue until a successful scan occurs or
        // the user explicitly cancels
        scanIntent.putExtra(ScanActivity.INTENT_EXTRA_RETRY_ON_ERROR, true);

        // Kick off the scan activity
        this.startActivityForResult(scanIntent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == SCAN_REQUEST_CODE)
        {
            // Track that this activity has already asked for a scan
            this.hasRequestedScan = true;
            Toast.makeText(MainScreen.this, resultCode+"", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK)
            {
                PaymentCard scannedCard = data.getParcelableExtra(ScanActivity.INTENT_EXTRA_PAYMENT_CARD);
                List<String> errors = data.getStringArrayListExtra(ScanActivity.INTENT_EXTRA_SCAN_ERRORS);

                // Handle the scan result
                this.onScanResult(scannedCard, errors);

            }
            else if (resultCode == ScanActivity.RESULT_NO_NFC)
            {
                // This device does not have an NFC sensor
                new AlertDialog.Builder(this)
                        .setTitle("Device has no NFC Sensor")
                        .setMessage("In order to scan a payment card, you must have a device with an NFC sensor.")
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }
            else if (resultCode == ScanActivity.RESULT_CANCELED)
            {
                // The scanning was cancelled by the user
            }
        }
        else
        {
            // Let the parent handle this, we don't know what it is
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onScanResult(PaymentCard cardInformation, List<String> errors)
    {
        // NOTE: The errors list would contain any errors the scanning may have yielded
        //Toast.makeText(MainScreen.this, "good", Toast.LENGTH_SHORT).show();
        if (cardInformation != null)
        {
            t.setText("Card: " + cardInformation.getCardPreferredName()+" \nName: "+cardInformation.getCardholderName()+" \nLast 4 digits: "+cardInformation.getLastFourDigits()
            +"\nActivation date: "+cardInformation.getActivationDate()+"\nExpiry Date: "+cardInformation.getExpiryDate());
//            // Remove any previous cards
//            this.root.removeAllViews();
//
//            // Card was successfully read, create a new card view and add it to the layout so that the user can see the
//            // card information
//            CardView cardView = new CardView(this.root, cardInformation, this);
//            LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            cardViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//            this.root.addView(cardView, 0, cardViewLayoutParams);

        } else {
            t.setText("null");
        }
    }
}
