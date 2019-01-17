package com.example.jordan.localtext;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by jordan on 17/02/18.
 * @version March 4th 2018
 * WIFI_STATUS_CHANGED Broadcast receiver for updating our app when the wifi status changes.
 */

public class WifiStatusChangedReceiver extends BroadcastReceiver{
    /**
     * Closes the server connection since the connection has been lost.
     * @param context
     * @param intent
     */
    public static final int PRIORITY = 500;
    public void onReceive(Context context,Intent intent){
        System.out.println("Wifi Status Changed");
        //Determining the current activity open.
        if(context instanceof ConnectActivity){
            System.out.println("Called during CONNECT ACTIVITY");
            updateConnectActivity((ConnectActivity)context);
        }
        else if(context instanceof  ServerActivity){
            System.out.println("Called during SERVER ACTIVITY");
            ServerActivity activity = (ServerActivity)context;
            updateServerActivity(activity);
        }
    }
    public void updateServerActivity(ServerActivity context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            context.resetConnection();
        }
    }
    public void updateConnectActivity(ConnectActivity context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        System.out.println("Wifi State:" + wifiManager.getConnectionInfo().getSupplicantState().toString());
        if(wifiManager.isWifiEnabled()){
            System.out.println("Wifi is enabled");
            //Starts a thread to update the activity once the wifi connection has completed.
            Thread connectionThread = new Thread(new WaitForWifiConnectionCompletedTask(context,wifiManager));
            connectionThread.start();
        }
        else{
            System.out.println("Wifi is disabled");
            context.obtainIP();
        }


    }

    /**
     * Class for the activity that will wait until the wifi connection to be completed.
     */
    private class WaitForWifiConnectionCompletedTask implements Runnable{
        public ConnectActivity activity;
        public WifiManager wifiManager;
        public WaitForWifiConnectionCompletedTask(ConnectActivity activity,WifiManager wifiManager){
            this.activity = activity;
            this.wifiManager = wifiManager;
        }

        /**
         * Waits until the wifi connection activity has finished, then updates the connect activity.
         */
        public void run(){
            //Waits until the wifiManager has a proper IP Address and updates the connect activity.
            while(wifiManager.getConnectionInfo().getIpAddress() == 0){
                try {
                    Thread.sleep(150);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            System.out.println(wifiManager.getConnectionInfo().getSupplicantState());
            //Runs the obtain IP method on the UI Thread.
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.obtainIP();
                }
            });

        }
    }
    /**
     * Creates an IntentFilter to use for this broadcast receiver.
     * @return the wifi intent filter.
     */
    public IntentFilter wifiFilter(){
        IntentFilter wifiIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiIntentFilter.setPriority(PRIORITY);
        return wifiIntentFilter;
    }
}
