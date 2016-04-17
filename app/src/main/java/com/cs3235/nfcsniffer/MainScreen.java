package com.cs3235.nfcsniffer;

/**
 * Created by mingxuan on 5/4/2016.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import java.nio.ByteBuffer;


public class MainScreen extends AppCompatActivity {
    public NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "Card detected", Toast.LENGTH_LONG).show();
        try {

            byte[] selectPSEDirectory = {
                    (byte)0x00,
                    (byte)0xA4,
                    (byte)0x04,
                    (byte)0x00,
                    (byte)0x0E,
                    (byte)0x31,
                    (byte)0x50,
                    (byte)0x41,
                    (byte)0x59,
                    (byte)0x2E,
                    (byte)0x53,
                    (byte)0x59,
                    (byte)0x53,
                    (byte)0x2E,
                    (byte)0x44,
                    (byte)0x44,
                    (byte)0x46,
                    (byte)0x30,
                    (byte)0x31
            };
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep iso = IsoDep.get(tag);
            iso.connect();
            byte[] selectPSEDirectoryResult = iso.transceive(selectPSEDirectory);
            String PSEDirectory = decode(byteToHex(selectPSEDirectoryResult));
            Log.d("PSEDirectoryHex", byteToHex(selectPSEDirectoryResult));
            Log.d("PSEDirectory", PSEDirectory);

            byte[] read = {
                    (byte)0x00,
                    (byte)0xB2,
                    (byte)0x01,
                    (byte)0x0C,
                    (byte)0x00
            };
            byte[] readResult = iso.transceive(read);
            String readResultString = decode(byteToHex(readResult));
            Log.d("readResultHex", byteToHex(readResult));
            Log.d("readResult", readResultString);

        } catch(Exception e) {
            Log.d("nfcDebugError", e.getMessage());
        }
        super.onNewIntent(intent);
    }

    public static String byteToHex(byte[] byteArray){
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for ( int j = 0; j < byteArray.length; j++ ) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String decode(String hex){

        String str = "";
        for (int i = 0; i < hex.length();i+=2)
        {
            String s = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        return str;

    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[]intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
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
}
