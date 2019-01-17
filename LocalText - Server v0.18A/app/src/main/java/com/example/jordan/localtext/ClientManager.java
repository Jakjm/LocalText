package com.example.jordan.localtext;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.net.Socket;
/**
 * Created by jordan on 07/02/18.
 * @author jakjm
 * @version v0.17 March 5th 2018
 * This class is meant to manage the ClientHandler for the purpose of each client in our app.
 * It makes the program a lot more modular.
 */

public class ClientManager {
    private ClientHandler client;
    private Thread sendingThread; //Thread for sending to the client any messages FROM the server.
    private Thread receivingThread; //Thread for receiving any message sent BY the client.
    public ArrayList <String> messagesToSend;
    public static final String TEXT = "TEXT";
    public static final String DELIVER = "DELIVER";
    public static final String SHUTDOWN = "SHUTDOWN";
    public static final String OK = "OK";
    public static final String FAIL = "FAIL";
    public boolean connected; //Used for checking if the threads should exit and thereby destroy themselves.
    public ServerActivity owningActivity;
    public ArrayList <Message> previousSentMessageList;
    /*
   * Char arrays for characters that will be used in the passcode.
    */
    public static final char [] PASS_LETTERS = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    public static final char [] PASS_NUMBERS = {'0','1','2','3','4','5','6','7','8','9'};
    public static final char [] PASS_PUNCT = {'!','@','#','$','%','.',':',';','<','>'};
    public static final int PASS_LENGTH = 5;
    public ClientManager(Socket socket,String passcode,ServerActivity owningActivity){
        this.client = new ClientHandler(socket);

        //If the user fails to enter the correct passcode, shuts down.
        if(!passcodePass(passcode)){
            shutdown();
            return;
        }
        messagesToSend = new ArrayList<String>();
        printContacts();
        connected = true;

        //Creates the thread to run the send messages task.
        sendingThread = new Thread(new SendMessagesTask());
        sendingThread.start();


        //Creates the thread to run the take messages task.
        receivingThread = new Thread(new TakeMessagesTask());
        receivingThread.start();
        this.owningActivity = owningActivity;
        previousSentMessageList = new ArrayList<Message>();
    }

    /**
     * Sees whether the client has entered a valid passcode or not.
     * @param passcode
     * @return
     */
    private boolean passcodePass(String passcode){
            String passAttempt = client.read(); //Reads the attempt at the passcode from the user.
            //If the attempt is a null string.
            if(passAttempt == null){
                client.write(FAIL,true);
                return false;
            }
            else if(passAttempt.equals(passcode)){  //If the passcode succeeds to match, return true.
                client.write(OK,true);
                return true;
            }
            else {
                client.write(FAIL, true);
                return false; //Otherwise, we return false.
            }
    }
    public String ip(){
        return client.ip();
    }
    /**
     * Prints out the entire list of contacts to the client.
     */
    private void printContacts(){
        client.write(""+ ServerActivity.contactsList.size(),true);
        for(Contact c : ServerActivity.contactsList){
            client.flushlessWrite(c.toString(),true);
        }
        client.flush(); //Since we wrote without flushing, we need to flush the writer.
    }
    /**
     * Runnable classes for the thread task.
     */
    private class TakeMessagesTask implements Runnable{
        public void run(){takeMessages();}
    }
    /**
     * Takes messages from the client.
     * Reads a command, i.e to send a text, then executes the command.
     */
    private void takeMessages(){
        while(true){
            String action = client.read();
            if(action == null || action.equals(SHUTDOWN)){
                shutdown();
                return; //Break out of the loop.
            }
            else if(action.equals(TEXT)) {
                receiveMessage();
            }
        }
    }
    /**
     * Reads the message (that the phone should send out) from the computer,
     * then sends that message via SMS
     */
    private void receiveMessage(){
        String number = client.read();
        String receivedMessage = client.read();
        previousSentMessageList.add(new Message(number,receivedMessage)); //Adds the message to a list of messages already sent.
        if(!validateMessage(number,receivedMessage))return; //Protects in the event we get
        sendSMS(number, receivedMessage);
    }

    /**
     * Ensures that the number and message we have received are valid.
     * @param number
     * @param receivedMessage
     * @return
     */
    private boolean validateMessage(String number,String receivedMessage){
        if(number == null || receivedMessage == null)return false;
        number = formatNumber(number); //Properly formats the number.
        if(receivedMessage.equals("") || number.equals("") || number.length() < 10) return false;
        return true;
    }

    /**
     * Formats a number to have only numerical content.
     * Gets rid of the +1 for long distance as well.
     * @param number
     * @return the formatted phone number. OR "" if the number doesn't format properly.
     */
    private static String formatNumber(String number) {
        String formattedNumber = number.trim();
        try {
            //Get rid of the long distance +1
            if(formattedNumber.substring(0, 2).equals("+1")) {
                formattedNumber = formattedNumber.substring(2);
            }
            else if(formattedNumber.substring(0,1).equals("1")) {
                formattedNumber = formattedNumber.substring(1);
            }
            for(int i = 0;i < formattedNumber.length();i++) {
                if(!Character.isDigit(formattedNumber.charAt(i))) {
                    formattedNumber = formattedNumber.substring(0,i) + formattedNumber.substring(i+1);
                    i--; //Move back an index since the current index has just been deleted.
                }
            }
        }
        catch(Exception e) {
            return "";
        }
        return formattedNumber;
    }
    /**
     * Shuts down the client manager.
     *
     */
    public void shutdown(){
        connected = false;
        client.closeCommunications();
        owningActivity.clientList.remove(this);
        //Calls the clients modified method
        owningActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                owningActivity.onClientsModified();
            }
        });
    }
    /**
     * Runnable class for the thread task.
     * Slightly innefficient as it runs even if not connected but we'll leave it like this for now.
     */
    private class SendMessagesTask implements Runnable{
        public void run(){
            sendMessages();
        }
    }

    /**
     * Creates a passcode.
     * @return the passcode, if correctly formed or recursively returns itself until the passcode is correctly formed.
     */
    public static String createPasscode() {
        int randomNumber = 0; //Variable to store the random number at this current moment in time.
        String pass = "";
        for (int i = 0; i < PASS_LENGTH; i++) {
            randomNumber = (int) (Math.random() * 3);
            //Random number chooses the character set, then the character within that set that we're
            //adding to the passcode.

            //If the random number is zero, we pick from letters :)
            if (randomNumber == 0) {
                randomNumber = (int) (Math.random() * PASS_LETTERS.length);
                pass += PASS_LETTERS[randomNumber];
            }
            //If the random number is 1, we pick from punctuation points :)
            else if (randomNumber == 1) {
                randomNumber = (int) (Math.random() * PASS_PUNCT.length);
                pass += PASS_PUNCT[randomNumber];
            }
            //Else if the random number is 3, we pick from numbers/digits okay honey?
            else {
                randomNumber = (int) (Math.random() * PASS_NUMBERS.length);
                pass += PASS_NUMBERS[randomNumber];
            }
        }
        if (validatePasscode(pass)) {
            return pass;
        } else {
            return createPasscode();
        }
    }

    /**
     * Validates that the passcode is built to specification.
     * @param passCode
     * @return whether the passcode is valid or not.
     */
    public static boolean validatePasscode(String passCode){
        boolean punctCheck = false;
        boolean numCheck = false;
        boolean letterCheck = false;

        //Looping through each of the letters of the pass code.
        for(int i = 0;i < passCode.length();i++){
            char currentCharacter = passCode.charAt(i); // Get the current character within the string.
            //If we have not passed the punctuation check, perform it.
            if(!punctCheck) {
                for (int b = 0; b < PASS_PUNCT.length;b++){
                    if(currentCharacter == PASS_PUNCT[b]){
                        punctCheck = true;
                        break;
                    }
                }
                /*
                *If we've passed the punctuation check, continue to the next character.
                * Since if the character is a punctuation point, it won't be a letter or a number.
                 */
                if(punctCheck)continue;
            }
            //If we have not passed the number check, perform it.
            if(!numCheck){
                for (int b = 0; b < PASS_NUMBERS.length;b++){
                    if(currentCharacter == PASS_NUMBERS[b]){
                        numCheck = true;
                        break;
                    }
                }
                /*
                *If we've just passed the num check, continue to the next character.
                *Since if the character is a number, it won't be a letter or the punctuation point.
                 */
                if(numCheck)continue;
            }
            //If we have not passed the letter check, perform it.
            if(!letterCheck){
                for(int b = 0;b < PASS_LETTERS.length;b++){
                    if(currentCharacter == PASS_LETTERS[b]){
                        letterCheck = true;
                        break;
                    }
                }
                /*
                *If we've passed the letter check, continue to the next character.
                * Since if the character is letter, it won't be a punctuation point or a number.
                 */
                if(letterCheck)continue;
            }
        }
        /*
        * If we passed all three checks, return true. Else, we return false.
         */
        if(numCheck && punctCheck && letterCheck)return true;
        else return false;
    }

    /**
     * Sends each of the messages to the client.
     * ie. Send TEXT then 612333213 then the message. ONE BY ONE though
     */
    private void sendMessages(){
        while(true){
            if(!connected)return; //Do this to ensure that the sending thread shuts down once the client has disconnected.
            while(messagesToSend.size() > 0){
                client.write(messagesToSend.get(0),true);
                messagesToSend.remove(0);
            }
        }
    }
    /**
     * Sends a SMS text message via the sms manager.
     * @param number - the number that the text is being sent to.
     * @param message - the message that is being sent.
     */
    public static void sendSMS(String number,String message){
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number,null,message,null,null);
    }
    
}
