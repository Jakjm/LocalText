package com.example.jordan.localtext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Class for the reception of a text message
 * @version 0.17 March 5th 2018
 */
public class ReceivedSMSReceiver extends BroadcastReceiver {
    public SmsManager manager = SmsManager.getDefault();
    public static final int PRIORITY = 500;
    public void onReceive(Context context, Intent intent){
        //Gets the server activity the broadcast receiver is registered to.
       ServerActivity serverActivity = null;
       if(context instanceof  ServerActivity)serverActivity = (ServerActivity)context;

        System.out.println("Received SMS Message");
        //Gets stored data from the intent.
        Message [] receivedMessages = null;
        final Bundle bundle = intent.getExtras();
        try{
            if(bundle != null){
                final Object [] pdusObjects = (Object [])bundle.get("pdus");
                receivedMessages = new Message[pdusObjects.length];
                for(int i = 0;i < pdusObjects.length;i++){
                    Message thisMessage = getMessage((byte[])pdusObjects[i]);
                    receivedMessages[i] = thisMessage;
                    add_Message_to_Client(thisMessage,serverActivity);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the main class to add the message to each of the client's outbound messages.
     * @param message
     */
    public void add_Message_to_Client(Message message,ServerActivity serverActivity){
        for(ClientManager client : serverActivity.clientList){
            client.messagesToSend.add(ClientManager.TEXT);
            client.messagesToSend.add(message.number);
            client.messagesToSend.add(message.message);
        }
    }
    /**
     * Gets the message from the message data.
     * @param messageData
     */
    public Message getMessage(byte [] messageData){
        SmsMessage currentMessage = SmsMessage.createFromPdu(messageData);
        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
        String message = currentMessage.getMessageBody();
        return new Message(phoneNumber,message);
    }

    /**
     * Creates an intent filter to use for this broadcast receiver.
     * @return the intent filter.
     */
    public IntentFilter smsReceivedFilter(){
        IntentFilter smsFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        smsFilter.setPriority(PRIORITY);
        return smsFilter;
    }
}