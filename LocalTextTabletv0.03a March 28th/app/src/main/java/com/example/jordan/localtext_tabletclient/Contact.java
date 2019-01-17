package com.example.jordan.localtext_tabletclient;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jordan on 09/01/18.
 * Class for a contact read from the contacts list.
 * @version January 10th 2018
 * @author Jordan Malek;
 */
public class Contact implements Serializable{
    public String name;
    public String number; //Phone number
    public String id;
    public String messages;
    public static final String RECEIVED = "Them:";
    public static final String SENT = "You:";
    public Contact(String name,String id,String number){
        this.id = id;
        this.name = name;
        this.number = number;
        this.messages = "";
    }
    /**
     * Constructs a client from the given client data string - originally generated from the toString() method that belongs to the server.
     * @param clientString
     */
    public Contact(String clientString){
        this.name = clientString.substring(0,clientString.indexOf('|'));
        this.number = formatNumber(clientString.substring(clientString.indexOf('|')+1,clientString.lastIndexOf('|')));
        this.id = clientString.substring(clientString.lastIndexOf('|')+1);
        this.messages = "";
    }
    /**
     * Tries to get the contact matching the phone number.
     */
    public static Contact getContactFromNumber(String number,ArrayList<Contact> contactList) {
        for(Contact currentContact : contactList) {
            if(formatNumber(currentContact.number).equals(formatNumber(number))) {
                return currentContact;
            }
        }
        return null;
    }
    /**
     * Formats a number to have only numerical content.
     * Gets rid of the +1 for long distance as well.
     * @param number
     * @return the formatted phone number. OR "" if the number doesn't format properly.
     */
    public static String formatNumber(String number) {
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
     * Creates a string version of this contact.
     * @return the string formed by this contact.
     */
    public String toString(){
        return "" + name + "|" + number + "|" + id;
    }
    /**
     * Adds a string message to the contact's message history.
     * @param message - the message that is to be added.
     * @param action - the action that was done to send the message (Received or sent)
     */
    public void addMessage(String message,String action) {
    	if(action.equals(RECEIVED)) {
    		messages += "Them:" + message + "\n";
    	}else if(action.equals(SENT)) {
    		messages += "You:" + message + "\n";
    	}
    }
}
