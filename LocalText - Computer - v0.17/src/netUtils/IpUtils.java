package netUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
/**
 * This class retrieves IP Addresses using the java network interface class.
 * @author jordan
 * @version February 10th 2018 v0.15
 * 
 */
public class IpUtils {
	/*
	According to RFC 1918 these are the addresses for private/local addresses.
	10.0.0.0        -   10.255.255.255  (10/8 prefix)
	172.16.0.0      -   172.31.255.255  (172.16/12 prefix)
	192.168.0.0     -   192.168.255.255 (192.168/16 prefix)
	 */
	public final static String [] PRIVATE_ADDRESSES = {"192.168","10","172"};
	private static final int PING_TIME = 1200;
	/**
	 * Returns the subnet of the given string ipAddress.
	 * @return
	 */
	private static String subnet(String ip) {
		return ip.substring(0, ip.lastIndexOf('.'));
	}
	/**
	 * Returns the local inet address
	 * @return the inet address of the local connection or null if no such address exists.
	 */
	public static InetAddress getLocalIP() {
		Enumeration<NetworkInterface> networkList = null; 
		InetAddress localAddress = null;
		try {
			//Get the list of network interfaces.
			networkList = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		//Loop through the network interfaces
		while(networkList.hasMoreElements()) {
			//Get each inet address within the current network interface.
			Enumeration<InetAddress> addressList = networkList.nextElement().getInetAddresses();
			while(addressList.hasMoreElements()) {
				InetAddress thisIP = addressList.nextElement();
				String ipString = thisIP.toString(); //Gets the string ip of the inetAddress.
				
				//If the ip is a local ip, return it. Checks based on the ip prefix.
				if(ipString.indexOf(PRIVATE_ADDRESSES[0]) == 1) {
					localAddress = thisIP;
				}
				else if(ipString.indexOf(PRIVATE_ADDRESSES[1]) == 1) {
					localAddress = thisIP;
				}
				else if(ipString.indexOf(PRIVATE_ADDRESSES[2]) == 1) {
					localAddress = thisIP;
				}
			}
		}
		return localAddress;
	}
	/**
	 * Scans the local network for ip addresses.
	 * @return an array list of local ip addresses.
	 */
	public static ArrayList<InetAddress> ipScan() {
		ArrayList<InetAddress> ipList = new ArrayList<InetAddress>();
		ArrayList<Thread> taskList = new ArrayList<Thread>();
		//Getting the local subnet.
		String subnet = subnet(getLocalIP().toString()).substring(1);
		for(int i = 0;i <= 255;i++) {
			String address = subnet + "." + i;
			Thread ipTaskThread = new Thread(new ipScanTask(address,ipList));
			taskList.add(ipTaskThread);
			ipTaskThread.start();
		}
		//Waits until all of the threads stop!
		for(Thread task : taskList) {
			try {
				task.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ipList;
	}
	/**
	 * Task for scanning for a specific IP address.
	 * @author jordan
	 * 
	 */
	private static class ipScanTask implements Runnable {
		private String address;
		private ArrayList<InetAddress>ipList;
		public ipScanTask(String address,ArrayList<InetAddress>ipList) {
			this.address = address;
			this.ipList = ipList;
		}
		public void run() {
			try {
				InetAddress currentAddress = InetAddress.getByName(address);
				if(currentAddress.isReachable(PING_TIME)) {
					ipList.add(currentAddress);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
