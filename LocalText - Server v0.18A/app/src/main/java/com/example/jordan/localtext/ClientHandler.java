package com.example.jordan.localtext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Basic class for controlling a connection with a connected client.
 * This class is meant to be generic; meaning it does not have anything that can ONLY
 * be used for this app.
 * @version February 8th 2018
 */
public class ClientHandler{
	private BufferedWriter writer;
	private BufferedReader reader;
	private Socket socket;
	
	public ClientHandler(Socket socket){
		this.socket = socket;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public String ip(){
		return socket.getInetAddress().toString();
	}
    /**
	 * Writes a new message to the client.
	 * Automatically flushes the writer so you don't have to.
	 * @param message - the message to be sent to the client.
	 * @param newLine - whether a new line should be added to the sequence.
	 */
    public void write(String message,boolean newLine){
    	try {
			writer.append(message);
			if(newLine){
				writer.append("\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	/**
	 * Writes a message without flushing. Useful where you'd like to flush after writing multiple lines instead
	 * of after each one.
	 */
    public void flushlessWrite(String message,boolean newLine){
		try{
			writer.append(message);
			if(newLine)writer.append("\n");
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Flushes the writer of this client handler.
	 */
	public void flush(){
    	try {
			writer.flush();
		}
		catch(IOException e){
    		e.printStackTrace();
		}
	}
	/**
	 * Method for writing an array list of String lines to the client.
	 * More efficient since it only flushes at the end of the list.
	 * @param messages
	 */
	public void writeArrayList(ArrayList<String> messages){
		try{
			for(String thisMessage : messages){
				writer.append(thisMessage);
				writer.append("\n");
			}
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * Method for writing an array of String lines to the client.
	 * More efficient since it only flushes at the end of the list.
	 * @param messages - The messages that should be sent to the server.
	 */
	public void writeArray(String[] messages){
		try{
			for(String thisMessage : messages){
				writer.append(thisMessage);
				writer.append("\n");
			}
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
    /**
     * Reads a new line from the bufferedReader.
     * Covers the IOException with a try-catch.
     * @return the latest line from the buffered reader.
     */
    public String read(){
    	String newLine = "";
    	try{
    		newLine = reader.readLine();
    	}catch(IOException e){
    	   e.printStackTrace();
    	}
    	return newLine;
    }
	public void closeCommunications() {
		//Shut down communications between client and user.
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
