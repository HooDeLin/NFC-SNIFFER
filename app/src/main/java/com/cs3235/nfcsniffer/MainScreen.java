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

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainScreen extends AppCompatActivity {
    public NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            Map<String, String> result = new HashMap<String, String>();
            result.put("psehex", byteToHex(selectPSEDirectoryResult));
            result.put("pse", PSEDirectory);
            result.put("readresulthex", byteToHex(readResult));
            result.put("readresult", readResultString);
            String url = "http://cs3235-2.appspot.com/cardinfo";
            makeRequest(url, result);

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

    public static Object makeRequest(String path, Map params) throws Exception
    {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(path);

        //convert parameters into JSON object
        JSONObject holder = getJsonObjectFromMap(params);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(holder.toString());

        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        return httpclient.execute(httpost, responseHandler);
    }

    private static JSONObject getJsonObjectFromMap(Map params) throws Exception {

        //all the passed parameters from the post request
        //iterator used to loop through all the parameters
        //passed in the post request
        Iterator iter = params.entrySet().iterator();

        //Stores JSON
        JSONObject holder = new JSONObject();

        //using the earlier example your first entry would get email
        //and the inner while would get the value which would be 'foo@bar.com'
        //{ fan: { email : 'foo@bar.com' } }

        //While there is another entry
        while (iter.hasNext())
        {
            //gets an entry in the params
            Map.Entry pairs = (Map.Entry)iter.next();

            //creates a key for Map
            String key = (String)pairs.getKey();

            //Create a new map
            Map m = (Map)pairs.getValue();

            //object for storing Json
            JSONObject data = new JSONObject();

            //gets the value
            Iterator iter2 = m.entrySet().iterator();
            while (iter2.hasNext())
            {
                Map.Entry pairs2 = (Map.Entry)iter2.next();
                data.put((String)pairs2.getKey(), (String)pairs2.getValue());
            }

            //puts email and 'foo@bar.com'  together in map
            holder.put(key, data);
        }
        return holder;
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
