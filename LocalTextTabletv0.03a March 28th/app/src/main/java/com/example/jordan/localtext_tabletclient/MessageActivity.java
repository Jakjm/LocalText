package com.example.jordan.localtext_tabletclient;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.StringTokenizer;


/**
 * Activity for sending text messages.
 * @author Jordan
 * @version March 12th 2018 v0.02
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    //Gui components
    EditText messageET;
    Button sendButton,backButton;
    TextView contactTV,numberTV;
    LinearLayout messagesPanel;
    Contact currentContact;
    private ClientManager clientManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getGUIComponents();
        clientManager = ConnectActivity.clientManager;
        sendButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        currentContact = MenuActivity.currentContact;
        ClientManager.messageActivity = this;
        this.updateFor(currentContact);
    }
    private void getGUIComponents(){
        messageET = (EditText)findViewById(R.id.messageET);
        sendButton = (Button)findViewById(R.id.sendButton);
        backButton = (Button)findViewById(R.id.backButton);
        contactTV = (TextView)findViewById(R.id.contactName);
        numberTV = (TextView)findViewById(R.id.contactNumber);
        messagesPanel = (LinearLayout)findViewById(R.id.messagesPanel);
    }

    /**
     * Updates the messaging activity for the contact that is given.
     * @param c - the contact that the message activity should be updated for.
     */
    public void updateFor(Contact c) {
        clearActivity();
        numberTV.setText(c.number);
        contactTV.setText(c.name);
        System.out.println(c.messages);
        if(!c.messages.equals("")) {
            /**
             * Gets each of the messages previously sent.
             */
            StringTokenizer messagesTokenizer = new StringTokenizer(c.messages, "\n");
            while (messagesTokenizer.hasMoreTokens()) {
                String message = messagesTokenizer.nextToken();
                TextView messageLabel = new TextView(this);
                messageLabel.setText(message);
                messageLabel.setTextSize(16);
                messagesPanel.addView(messageLabel);
            }
        }
    }

    /**
     * Clears the activity of any inputs still lingering.
     */
    public void clearActivity(){
        numberTV.setText("");
        contactTV.setText("");
        messagesPanel.removeAllViews();
        messageET.setText("");
    }

    /**
     * Method to send messages
     * Can't call this method directly from onclick since we'll get a NetworkOnMainThreadException.
     */
    private void sendMessage(String number,String message){
        clientManager.sendMessage(number,message);
    }

    /**
     * Sends a message.
     */
    private void send(){
        //Gets the name and number and verifies that we're ready to send the message.
        String number = Contact.formatNumber(numberTV.getText().toString());
        String message = messageET.getText().toString().trim();
        if(number.equals("") || message.equals(""))return;
        SendMessageTask sendTask = new SendMessageTask(number,message);
        sendTask.execute();
        //Updating now that the message has been sent.
        currentContact.messages += Contact.SENT + message + "\n"; //Adding it to the contact's messages.
        messageET.setText(""); //Clearing the message box.
        this.updateFor(currentContact);
    }
    public void onClick(View view){
        if(view == sendButton){
            send();
        }
        else if(view == backButton){
            super.onBackPressed();
            finishActivity(0);
        }
    }
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
    }
    public void refresh(){
        this.updateFor(currentContact);
    }
    /**
     * Class for the task to actually send the message via the client manager.
     * We use async task so that there isn't an exception thrown.
     */
    private class SendMessageTask extends AsyncTask<Void,Void,Void>{
        private String number;
        private String message;
        public SendMessageTask(String number,String message){
            this.number = number;
            this.message = message;
        }
        public Void doInBackground(Void ... params){
            sendMessage(number,message);
            return null;
        }
    }
}
