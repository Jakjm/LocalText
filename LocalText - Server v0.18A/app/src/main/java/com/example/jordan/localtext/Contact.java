package com.example.jordan.localtext;
/**
 * Created by jordan on 09/01/18.
 * Class for a contact read from the contacts list.
 * @version January 10th 2018
 * @author Jordan Malek;
 */
public class Contact{
    public String name;
    public String number; //Phone number
    public String id;
    public Contact(String name,String id,String number){
        this.id = id;
        this.name = name;
        this.number = number;
    }
    /**
     * Creates a string version of this contact.
     * @return the string formed by this contact.
     */
    public String toString(){
        return "" + name + "|" + number + "|" + id;
    }
}
