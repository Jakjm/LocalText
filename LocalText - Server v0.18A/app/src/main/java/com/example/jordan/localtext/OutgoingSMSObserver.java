package com.example.jordan.localtext;
import android.content.Context;
import android.database.ContentObserver;
import android.content.ContentResolver;
import android.os.Handler;
import android.net.Uri;
import android.database.Cursor;
/**
 * Created by jordan on 30/03/18.
 * @version March 30th 2018 v0.18
 * TODO: Clean this up!
 * @author Jordan
 */
import java.util.ArrayList;
public class OutgoingSMSObserver extends ContentObserver{
    ContentResolver resolver;
    private static final String COLUMN_TYPE = "type";
    private static final int TYPE_SENT = 2;
    private static final int MESSAGES_TO_CHECK = 4; //Messages to check if a recent one matches.
    private ArrayList<Message> previousMessageList;
    private ServerActivity serverActivity;
    public OutgoingSMSObserver(Handler handler,Context context){

        super(handler);
        this.serverActivity = (ServerActivity)context; //Getting the activity instance.
        //Sets up the content resolver
        resolver = context.getContentResolver();
        resolver.registerContentObserver(Uri.parse("content://sms"),true,this);
        System.out.println("Registering observer");
        previousMessageList = new ArrayList<Message>();
    }
    public void unregister(){
        resolver.unregisterContentObserver(this);
    }
    //Called when there is a new update...
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //System.out.println("Observer updating!");
        Uri uriSMSURI = Uri.parse("content://sms");
        Cursor cur = resolver.query(uriSMSURI, null, null, null, null);
        cur.moveToNext();

        String content = cur.getString(cur.getColumnIndex("body"));
        String smsNumber = cur.getString(cur.getColumnIndex("address"));
        if (smsNumber == null || smsNumber.length() <= 0) {
            smsNumber = "Unknown";
            cur.close();
            return;
        }
        int type = cur.getInt(cur.getColumnIndex(COLUMN_TYPE));
        //If the message was an outgoing one...
        if(type==TYPE_SENT){
            //Creates a new message.
            Message message = new Message(smsNumber,content);
            //If the message is NOT an old one, then we've validated it.
            if(checkIfOld(message)){
                cur.close();
                return;
            }
            previousMessageList.add(message);
            System.out.println("New outgoing message!");
            System.out.println(message.number + " " + message.message);
            sendToClients(message);
        }
        cur.close();
    }
    //TODO: CLEAN THIS MESS
    public void sendToClients(Message message){
        //Checks to see if there is a client we should ignore, since the message came from them.
        //Otherwise, prints the message to each and every client.
        ClientManager ignoreClient = null;
        for(ClientManager client : serverActivity.clientList){
            //Checks the last four messages that the client sent.
            if(client.previousSentMessageList.size() > MESSAGES_TO_CHECK) {
                for (int i = client.previousSentMessageList.size() - MESSAGES_TO_CHECK; i < client.previousSentMessageList.size(); i++) {
                    //If the messages match
                    if (message.equals(client.previousSentMessageList.get(i))) {
                        ignoreClient = client;
                        break; //If we've found a client that has a matching message, break out of the loop.
                    }
                }

            }
            else{
                for(Message checkMessage : client.previousSentMessageList){
                    if(message.equals(checkMessage)){
                        ignoreClient = client;
                        break;
                    }
                }
            }
            if (ignoreClient != null)
                break; //If we've found a client that has a matching message, break out of the loop.
        }
        //Loops through the clients that aren't the ignore client, and sends the delivery dialogue to them
        for(ClientManager client : serverActivity.clientList){
            //If this is the ignore client, skip it.
            if(ignoreClient != null && ignoreClient == client){
                continue;
            }
            client.messagesToSend.add(ClientManager.DELIVER);
            client.messagesToSend.add(message.number);
            client.messagesToSend.add(message.message);
        }
    }
    //Checks if the message has already been sent before during this program.
    public boolean checkIfOld(Message checkMessage){
        //If we find a matching message, we return true. Else, we return false.
        for(Message message : previousMessageList){
            //System.out.println(message.toString() + " || " + checkMessage.toString());
            if(message.equals(checkMessage)){
                return true;
            }
        }
        return false;
    }
}
