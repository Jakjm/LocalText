package com.example.jordan.localtext;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.res.Configuration;

import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;


/**
 * @version 0.18 March 28th 2018
 * @author Jordan
 * Activity for once the server has been launched.
 */
public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    //The ServerSocket to create.
    public ServerSocket server;
    public final int PORT_NO = 4025;
    public final int MAX_CLIENTS = 3;
    public static ArrayList<Contact> contactsList;
    public Thread connectThread;
    public ArrayList <ClientManager> clientList;
    public boolean connected = false;
    public ReceivedSMSReceiver smsReceiver;
    public OutgoingSMSObserver sentObserver;
    public WifiStatusChangedReceiver wifiReceiver;
    public TextView passCode;
    public TextView ipText;
    public TextView messageText;
    public TextView clientDisplayLabel;
    public TextView uptimeTV;
    public LinearLayout clientLayout;
    public Button stopConnectionButton;
    public Button kickButton;
    public long uptime;
    public int displayMode; //The display mode for clients.
    public static final int DISPLAY_MODE=0;
    public static final int KICK_MODE = 1;
    public static final String KICK_LABEL = "Kick Clients";
    public static final String DISPLAY_LABEL = "Display Clients";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server);
        String ip = getIntent().getStringExtra("IP");

        //Getting our gui components
        messageText = (TextView)findViewById(R.id.message);
        passCode = findViewById(R.id.passCode);
        ipText = findViewById(R.id.ipAddress);
        ipText.setText("IP Address:" + ip);
        clientList = new ArrayList<ClientManager>();

        stopConnectionButton = findViewById(R.id.stopConnection);
        stopConnectionButton.setOnClickListener(this);


        displayMode = DISPLAY_MODE;
        clientLayout = findViewById(R.id.activeClientsLayout);
        clientDisplayLabel = findViewById(R.id.activeClientsTV);
        kickButton = findViewById(R.id.kickButton);
        kickButton.setOnClickListener(this);
        kickButton.setEnabled(false);

        //Gets the app to start connecting.
        startConnecting();
        startWifiChangedReceiver();
        startUptimeCounter();
    }
    //Starts the uptime counter at the bottom of the Server activity.
    public void startUptimeCounter(){
        uptimeTV = findViewById(R.id.uptimeTV);
        uptime = System.currentTimeMillis(); //Storing the starting time.
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) { //Run for the length of the program
                    //Calculate the formatted time and update the gui.
                    long currentTime = System.currentTimeMillis() - uptime;
                    long seconds = currentTime / 1000; //Getting the current time in seconds.
                    int hours = (int) Math.floor(seconds / 3600); //Getting the current time in hours.
                    seconds -= (hours * 3600);
                    int minutes = (int) Math.floor(seconds / 60); //Getting the current time in minutes.
                    seconds -= (minutes * 60);
                    final String time = String.format("Uptime %02d:%02d:%02d", hours, minutes, seconds);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            uptimeTV.setText(time);
                        }
                    });
                }
            }
        });
        updateThread.start();
    }
    /**
     * This method handles when the configuration of the app changes.
     * The manifest changes fix an issue where the app is relaunched when the screen is rotated.
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    /**
     * Starts the SMS Received receiver that we use while this activity is active.
     */
    public void startSMSReceiver(){
        smsReceiver = new ReceivedSMSReceiver();
        this.registerReceiver(smsReceiver,smsReceiver.smsReceivedFilter());
    }
    /**
    * Starts the Wifi Status Changed receiver that we use while this activity is active.
     */
    public void startWifiChangedReceiver(){
        wifiReceiver = new WifiStatusChangedReceiver();
        this.registerReceiver(wifiReceiver,wifiReceiver.wifiFilter());
    }

    /**
     * This method is called when the number of clients has increased or decreased.
     * Updates the activity's displays.
     */
    public void onClientsModified(){
        updateKickButton();
        updateClientLabel();
        if(displayMode==DISPLAY_MODE){
            makeClientLabels();
        }
        else if(displayMode==KICK_MODE){
            makeClientButtons();
        }
    }
    public void updateKickButton(){
        if(clientList.size() == 0){
            kickButton.setEnabled(false);
            kickButton.setText(KICK_LABEL);
            displayMode = DISPLAY_MODE;
        }
        else{
            kickButton.setEnabled(true);
        }
    }
    //Updates the label for the clients display
    public void updateClientLabel(){
        if(clientList.size() == 0){
            clientDisplayLabel.setText("No Active Clients");
        }
        else if(displayMode == DISPLAY_MODE){
            clientDisplayLabel.setText("Active Clients");
        }
        else{
            clientDisplayLabel.setText("Pick a Client to kick!");
        }
    }
    /**
    * Makes Client Buttons to display to the user.
     */
    public void makeClientButtons(){
        //For each of the clients
        clientLayout.removeAllViews();
        for(ClientManager client : clientList){
            //Construct a button and add it.
            Button clientButton = new Button(this);
            String labelText = (clientList.indexOf(client)+1) + ") "+ client.ip();
            clientButton.setText(labelText);
            clientButton.setGravity(Gravity.CENTER_HORIZONTAL);
            clientButton.setOnClickListener(this);
            clientLayout.addView(clientButton);
        }
    }

    /**
     * Makes Client Labels to display to the user.
     */
    public void makeClientLabels(){
        //For each of the clients
        clientLayout.removeAllViews();
        for(ClientManager client : clientList){
            TextView clientLabel = new TextView(this);
            String labelText = (clientList.indexOf(client)+1) + ") " + client.ip();
            clientLabel.setGravity(Gravity.CENTER_HORIZONTAL);
            clientLabel.setText(labelText);
            clientLayout.addView(clientLabel);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        /*
         * Resets the app in the event we're no longer connected.
         */
        if(!connected){
            Intent resetIntent = new Intent(getApplicationContext(),ConnectActivity.class);
            startActivity(resetIntent);
            finish();
        }
    }
    /**
     * Prevents the app from closing once the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        //Does nothing.
    }
    /*
    * Attempt to shut down communications with each client before the app closes.
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeConnection(true);
    }
    /**
     * Tells the app to start connecting with clients.
     */
    public void startConnecting(){
        //Gets each of the contacts
        new ContactsTask().execute();
        //Starting to receive sms received/delivered broadcasts.
        startSMSReceiver();
        sentObserver = new OutgoingSMSObserver(new android.os.Handler(),this);
        //Connects the server network
        ConnectTask networkTask = new ConnectTask();
        networkTask.execute(PORT_NO);
        connected = true;
        messageText.setText("Server Connected!");
    }
    /**
     * Inner class for connecting to the network asynchronously.
     */
    private class ConnectTask extends AsyncTask<Integer,Void,Void> {
        public Void doInBackground(Integer ... params){
            connect(PORT_NO);
            return null;
        }
        @Override
        protected void onPostExecute(Void nullVoid){
        }
    }

    /**
     * Creates the server socket and initializes the connection of new clients.
     * @param PORT_NO
     */
    public void connect(int PORT_NO){
        try {
            server = new ServerSocket(PORT_NO); //Connects the Server Socket using the port number.
        }catch(IOException e) {
            e.printStackTrace();
        }
        //Creates a concurrent thread for accepting new clients.
        connectThread = new Thread(new AcceptClientsTask());
        connectThread.start();  //Starts the thread concurrently.
    }




    /**
     * Accepts a new client and adds it to the list of clients that are working with our server.
     */
    public void acceptNewClient(){
        try {
            if(server.isClosed()){
                return; //Gets out of the method if the client has been closed.
            }
            String passcode = ClientManager.createPasscode();
            setPassCodeTextView(passcode);
            Socket newSocket = server.accept();
            //Creates a passcode for the client to enter.
            ClientManager newClient = new ClientManager(newSocket,passcode,this);
            clientList.add(newClient);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Sets the passcode text view to read the current passcode.
     */
    public void setPassCodeTextView(final String text){
        runOnUiThread(new Runnable(){
            public void run(){
                passCode.setText("Passcode " + text);
            }
        });
    }
    /**
     * Class for the task of accepting new clients.
     */
    public class AcceptClientsTask implements Runnable{
        public void run(){
            while(true){
                if(!connected){
                    return;
                }
                else if(clientList.size() < MAX_CLIENTS){
                    acceptNewClient();
                    callDisplayUpdate();
                }
            }
        }
        private void callDisplayUpdate(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onClientsModified();
                }
            });
        }
    }
    /**
     * Inner class for getting the contacts asynchronously.
     */
    private class ContactsTask extends AsyncTask<Void,Void,Void>{
        public Void doInBackground(Void ... params){
            getContacts();
            return null;
        }
    }

    /**
     * Gets a list of contacts from the phone and adds them to our now initialized contactsList.
     */
    public void getContacts(){
        contactsList = new ArrayList<Contact>(); // Initializes contacts list.
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNo = null;

                /*
                Another query to get the phone number of the contact, if they have one.
                 */
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneNumberCursor = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (phoneNumberCursor.moveToNext()) {
                        phoneNo = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    // contactsText += name + phoneNo + "\n";
                    Contact newContact = new Contact(name,id,phoneNo);
                    contactsList.add(newContact);
                }
            }
        }
        cursor.close();
    }

    /**
     * On Click method for our activity. Part of the onClickListener interface.
     * @param view
     */
    public void onClick(View view){
        if(view == stopConnectionButton){
            resetConnection();
        }
        else if(view == kickButton){
            if(displayMode == KICK_MODE){
                displayMode = DISPLAY_MODE;
                kickButton.setText(KICK_LABEL);
            }
            else if(displayMode == DISPLAY_MODE){
                displayMode = KICK_MODE;
                kickButton.setText(DISPLAY_LABEL);
            }
            onClientsModified(); //Calls on clients modified to change the layout of it.
        }
        //Else if the clicked view was a client to kick...
        else{
            //Getting the client that was selected.
            ClientManager client  = clientList.get(clientLayout.indexOfChild(view));
            client.shutdown();
        }
    }

    /**
     * Resets the connection when the stop connection button has been pressed.
     */
    public void resetConnection(){
        closeConnection(true);
        this.unregisterReceiver(smsReceiver);
        this.sentObserver.unregister();
        this.unregisterReceiver(wifiReceiver);
        Intent resetIntent = new Intent(getApplicationContext(),ConnectActivity.class);
        startActivity(resetIntent);
        finish();
    }
    /**
     * Sends a SMS text message via the sms manager.
     * @param number - the number that the text is being sent to.
     * @param message - the message that is being sent.
     */
    public void sendSMS(String number,String message){
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number,null,message,null,null);
    }
    /**
     * Closes the connection between the server and it clients.
     */
    private void closeConnection(boolean sendShutdownMessage){
        connected = false;
        try {
            if(sendShutdownMessage) {
                for (ClientManager client : clientList) {
                    client.messagesToSend.add(ClientManager.SHUTDOWN);
                }
            }

            if(server != null)server.close();
            Thread.sleep(200); //Adds a delay to help the clients shutdown properly.
            if(connectThread != null)connectThread.join();

        }
        catch(InterruptedException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
