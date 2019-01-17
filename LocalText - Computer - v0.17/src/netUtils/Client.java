package netUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The client connection of the client.
 * @author jakjm
 * @version February 13th 2018 v0.15
 */
public class Client extends Socket{
	private BufferedWriter writer;
	private BufferedReader reader;
	private static final int TIMEOUT = 800; //Timeout for connection.
	public Client(String serverIP,int portNo) throws IOException {
		super();
		super.connect(new InetSocketAddress(serverIP,portNo),TIMEOUT);
		reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(this.getOutputStream()));
	}
	public Client() throws UnknownHostException, IOException{
		super(InetAddress.getLocalHost().getHostAddress(),4025);
		reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(this.getOutputStream()));
	}
	/**
	 * Writes a new message to the server.
	 * @param message - the message to be sent to the server.
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
     * In the event of java closing, shuts down communications.
     */
    public void shutCommunications(){
			//Shut down communications between client and user.
			try {
				writer.close();
				reader.close();
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    public boolean active() {
    	return !this.isClosed();
    }
    /**
     * Reads a new message from the server.
     * @return returns the server's latest message.
     * @throws IOException
     */
    public String read(){
    	String message = "";
    	try{
    		message = reader.readLine();
    		//System.out.println(message);
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return message;
    }
}