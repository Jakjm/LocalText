package com.example.jordan.localtext_tabletclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Class for the menu activity of the tablet client for the localtext app.
 * @version March 12th 2018 v0.03
 * @author Jordan
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher{
    private LinearLayout contactsPane;
    public ArrayList<Contact> contactList; //The list of contacts.
    public ArrayList<Contact> currentList; //The current list of contact search results on the screen.
    private ClientManager clientManager;
    public static Contact currentContact;
    //String keys
    private EditText searchBarET;
    private static final String CONTACT_LIST = "CONTACTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactList = (ArrayList<Contact>)this.getIntent().getExtras().get(CONTACT_LIST);
        clientManager = ConnectActivity.clientManager;
        setContentView(R.layout.activity_menu);
        contactsPane = (LinearLayout)findViewById(R.id.contactsPane);
        addContactsToPane();
        clientManager.startReading(this);
        searchBarET = (EditText)findViewById(R.id.searchBar);
        searchBarET.addTextChangedListener(this);
    }
    /*
    Handles when the configuration is changed.
     */
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
    }

    /**
     * Sets up the back button to do nothing since we don't want our activity to close.
     */
    public void onBackPressed(){

    }
    /**
     * Adds the contacts to our contacts panel.
     */
    public void addContactsToPane(){
        for(Contact contact : contactList){
            Button contactButton = new Button(this);
            contactButton.setText(contact.name);
            contactButton.setOnClickListener(this);
            contactsPane.addView(contactButton);
        }
        currentList = contactList;
    }

    /**
     * Refreshes the displayed contacts using the given search string.
     * @param searchQuery - the string that should produce a list of contacts for our display.
     */
    public void refreshContacts(String searchQuery){
        //Remove all the previous items from our contact list.
        contactsPane.removeAllViews();
        ArrayList<Contact> tempContactList = new ArrayList<Contact>();
        //Loops through the contact list to add contacts that match the criteria, sorted by tier.
        String upperQuery = searchQuery.toUpperCase();

        //Tier one match: upper case contact name matches upper case search query
        for(Contact contact : contactList){
            if(contact.name.toUpperCase().equals(upperQuery)){
                tempContactList.add(contact);
            }
        }
        //Tier two match: search query is contained within or equals the contact's number
        for(Contact contact : contactList){
            //Also ensures we don't add the same contact twice.
            if(contact.number.contains(searchQuery) && !tempContactList.contains(contact)){
                tempContactList.add(contact);
            }
        }
        //Tier three match: upper case search query contains upper case contact name, or vice versa
        for(Contact contact : contactList){
            String upperName = contact.name.toUpperCase();
            //Also ensures we don't add the same contact twice.
            if((upperQuery.contains(upperName)||upperName.contains(upperQuery)) && !tempContactList.contains(contact)){
                tempContactList.add(contact);
            }
        }
        //Now adds the temp list of contacts to our display.
        for(Contact contact : tempContactList){
            Button contactButton = new Button(this);
            contactButton.setText(contact.name);
            contactButton.setOnClickListener(this);
            contactsPane.addView(contactButton);
        }
        currentList = tempContactList;
    }

    /**
     * On Click-method for any of the contact buttons that have been pressed.
     * @param view
     */
    public void onClick(View view){
        Contact selectedContact = currentList.get(contactsPane.indexOfChild(view)); //Gets the contact that was selected.
        System.out.println(selectedContact);
        currentContact = selectedContact;
        startMessageActivity();
        //Clear the gui
        refreshContacts("");
        searchBarET.setText("");
    }
    //Methods for our search bar EditText to get search results for the inputted string of text.
    public void beforeTextChanged(CharSequence s,int x,int y,int z){}
    public void afterTextChanged(Editable s){}
    public void onTextChanged(CharSequence s,int x,int y,int z){
        String searchValue = searchBarET.getText().toString().trim();
        refreshContacts(searchValue);
    }
    public void startMessageActivity(){
        Intent textActivityIntent = new Intent(this,MessageActivity.class);
        this.startActivity(textActivityIntent);
    }

}
