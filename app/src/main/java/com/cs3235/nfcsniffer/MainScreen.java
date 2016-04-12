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
            byte[] APDUCommand = {
                    (byte) 0x00, // CLA Class
                    (byte) 0xA4, // INS Instruction
                    (byte) 0x04, // P1  Parameter 1
                    (byte) 0x00, // P2  Parameter 2
                    (byte) 0x07, // Length
                    (byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10 // AID
            };
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep iso = IsoDep.get(tag);
            iso.connect();
            byte[] result = iso.transceive(APDUCommand);
            String cardBrand = decode(byteToHex(result));
            Log.d("cardBrand", cardBrand);
            
            /*
            byte[] readRecord={(byte)0x00,(byte)0xB2,(byte)0x02,(byte)0x0C,(byte)0x00};
            byte[] result2 = iso.transceive(readRecord);
            char[] hexChars2 = new char[result.length * 2];
            for ( int j = 0; j < result2.length; j++ ) {
                int v = result2[j] & 0xFF;
                hexChars2[j * 2] = hexArray[v >>> 4];
                hexChars2[j * 2 + 1] = hexArray[v & 0x0F];
            }*/
            //Log.d("debug", new String(hexChars2));
            /*byte[] getProcessingOptions={(byte)0x80,(byte)0xA8,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x83,(byte)0x00,(byte)0x00};
            byte[] readRecord={(byte)0x00,(byte)0xB2,(byte)0x02,(byte)0x0C,(byte)0x00};
            byte[] result1 = iso.transceive(getProcessingOptions);
            byte[]result2 = iso.transceive(readRecord);
            Log.d("nfcDebug", new String(result));
            Log.d("nfcDebug", new String(result1));
            Log.d("nfcDebug", new String(result2));*/
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
