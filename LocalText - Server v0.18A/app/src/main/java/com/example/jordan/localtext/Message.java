package com.example.jordan.localtext;

/**
 * Created by jordan on 16/01/18.
 * @version January 16th 2018
 * @author Jordan Malek
 */
public class Message {
    public String number;
    public String message;
    public long sendTime;
    public static final long TIME = 3000; //If the messages are equal and within 3000ms of each other they are considered fully equal.
    public Message(String number,String message){
        this.number = number;
        this.message = message;
        sendTime = System.currentTimeMillis();
    }
    public String toString(){
        return this.number + " " + this.message;
    }
    /**
     * Checks if this same message has been sent within the last few seconds.
     * @param m
     * @return
     */
    public boolean equals(Message m){
        //If the numbers match...
        if(formatNumber(this.number).equals(formatNumber(m.number))){
            //System.out.println("Numbers equal.");
            //And the message matches
            if(m.message.equals(this.message)){
                //System.out.println("Messages equal.");
                //And the messages are within the same time frame.
                if(Math.abs(m.sendTime - this.sendTime) < TIME) {
                    //System.out.println("Full pass");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a formatted deep copy of the number.
     * @param number
     * @return the formatted deep copy of the given number.
     */
    private String formatNumber(String number){
        number = deepCopy(number);
        for(int i = 0;i < number.length();i++){
            if(!Character.isDigit(number.charAt(i)))
            {
                number = number.substring(0,i) + number.substring(i+1);
                i--;
            }
        }
        return number;
    }

    /**
     * Creates a deep copy of the given string that way the old string will not be modified.
     * @param givenString - The string we only want to copy
     * @return a deep copy of that given string.
     */
    private String deepCopy(String givenString){
        char [] newStringSeq = new char [givenString.length()];
        for(int i = 0;i < newStringSeq.length;i++){
            newStringSeq[i] = givenString.charAt(i);
        }
        return new String(newStringSeq);
    }
}
