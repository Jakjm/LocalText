package com.example.jordan.localtext;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * @version ~0.16 February 18th 2018
 * @author Jordan Malek
 * This is the starting activity for the LocalTextApp.
 */
public class ConnectActivity extends AppCompatActivity implements OnClickListener{
    public boolean havePermissions = true;
    public boolean receiverActive = false;
    public final int ESSENTIAL_PERMS_REQ = 1;
    public String ipAddress;
    public TextView ipText;
    public TextView messageText;
    public Button connectButton;
    public WifiStatusChangedReceiver wifiStatusChangedReceiver;
    /*
    The list of required permissions for the app.
     */
    public String [] REQUIRED_PERMISSIONS = {Manifest.permission.RECEIVE_SMS, //Needed to read texts
            Manifest.permission.SEND_SMS, //Needed to send texts
            Manifest.permission.READ_SMS, //Needed to read texts
            Manifest.permission.READ_CONTACTS, //Needed to read contacts
            Manifest.permission.INTERNET, //Needed to use wifi
            Manifest.permission.ACCESS_WIFI_STATE, //Needed to determine whether the wifi is connected and get IP address.
            Manifest.permission.RECEIVE_MMS, //Needed to get texted images and stuff
            Manifest.permission.ACCESS_NETWORK_STATE //Needed to check wifi status.
    };


    /*
     * ~~~LIFECYCLE METHODS ~~~
     */

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Getting our gui components.
        messageText = (TextView)findViewById(R.id.message);
        ipText = (TextView)findViewById(R.id.ipAddress);
        //Setting up connect button.
        connectButton =  (Button)findViewById(R.id.connect);
        connectButton.setOnClickListener(this); //See onClick method at the bottom of this class.

        checkEssentialPermissions();
        obtainIP();
    }
    /**
     * Overrides the onPause method to stop the broadcast receiver.
     */
    @Override
    public void onPause(){
        super.onPause();
        if(receiverActive)this.unregisterReceiver(wifiStatusChangedReceiver); //Removes the wifi status changed receiver.
    }

    /**
     * Overrides the onResume method to start the broadcast receiver.
     */
    public void onResume(){
        super.onResume();
        if(!receiverActive)startWifiReceiver();
        obtainIP();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    /**
     * Prevents the app from closing once the back button is pressed.
     */
    @Override
    public void onBackPressed() {
    }
    /**
     * Obtains the ip address to display for the user, using the wifi manager.
     */
    public void obtainIP(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //Checks to see if the wifi is actually connected.
        System.out.println(wifiManager.getConnectionInfo().getIpAddress());
        if(wifiManager.getConnectionInfo().getSupplicantState().equals(SupplicantState.COMPLETED)){
                ipAddress = getIP(wifiManager);
                ipText.setText("IP Address: " + ipAddress);
                messageText.setText("Wifi connected, ready to go! Press the launch button in order to start!");
                connectButton.setEnabled(true);
        }
        else{
            messageText.setText("Wifi not connected, cannot start");
            ipText.setText("");
            connectButton.setEnabled(false);
        }
    }


    /*
     * ~~~ END OF LIFECYCLE METHODS ~~~
     */
    /**
     * Gets the ip from the given wifi manager.
     * @param wifiManager - the wifi manager for the wifi connection of the phoen.
     * @return the converted IP Address.
     */
    public String getIP(WifiManager wifiManager){
        String ipString = "";
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        byte [] ipAddress = BigInteger.valueOf(ip).toByteArray();
        try{
           ipString = InetAddress.getByAddress(ipAddress).getHostAddress();
        }catch(Exception e){
            e.printStackTrace();
        }
        //For some reason Android gives the IP backwards. We need to flip it back.
        return flipIP(ipString);
    }

    /**
     * Returns the flipped IP of the given one.
     * @param ip - the ip provided
     * @return the properly formatted IP address.
     */
    public String flipIP(String ip){
        StringTokenizer tokenizer = new StringTokenizer(ip,".");
        String flippedIp = "";
        while(tokenizer.countTokens() > 1){
            flippedIp = "." + tokenizer.nextToken() + flippedIp;
        }
        flippedIp = tokenizer.nextToken() + flippedIp;
        return flippedIp;
    }

    /**
     * Checks if we're missing any vital permissions from the app, and if that is the case, requests them!
     */
    public void checkEssentialPermissions() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS, ESSENTIAL_PERMS_REQ);
                return;
            }
        }
    }
    //Message in case permission check failed.
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int [] grantResults){
        for(int i = 0;i < grantResults.length;i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                havePermissions = false;
                messageText.setText("LocalText needs more permissions in order to work for you!");
                return;
            }
        }
        havePermissions = true;
    }

    /**
     * Starts the WIFI_STATUS_CHANGED broadcast receiver to ensure that our app is notified in the event the user
     * connects or disconnects from wifi.
     */
    public void startWifiReceiver(){
        wifiStatusChangedReceiver = new WifiStatusChangedReceiver();
        this.registerReceiver(wifiStatusChangedReceiver,wifiStatusChangedReceiver.wifiFilter());
        receiverActive = true;
    }
    /**
     * The on click listener for our app.
     * Provides on click service to our connect button!
     * @param view - The view that was clicked that triggered the call of this method.
     */
    public void onClick(View view) {
        //Gets permissions in case we don't have them.
        if(!havePermissions){
            checkEssentialPermissions();
            return;
        }
        if(view == connectButton){
            startServerActivity();
        }
    }

    /**
     * Starts the server activity.
     */
    public void startServerActivity(){
        unregisterReceiver(wifiStatusChangedReceiver);
        receiverActive = false;
        Intent intent = new Intent(getApplicationContext(),ServerActivity.class);
        intent.putExtra("IP",ipAddress);
        this.startActivity(intent);
        finish();
    }
}
