# LocalText
LocalText local server texting application. 
I worked on this last year and made a few fun things out of it - including a Swing emoji panel. 
You host a server on your phone and connect with your computer or tablet using the respective clients.

The Android Phone Server and Tablet Client are both Android Studio Projects. Opening the projects in Android Studio should allow you
to run the applications on your device 
(if you have USB debugging enabled on your phone and have your device connected to your computer via USB)

I have also included an executable .jar file for the Computer Client. If you cannot execute the Jar file,
ensure java is installed on your computer.

The Server, when running on your phone, provides the IP Address and Passcode so that you can connect using your respective Client.
The PC client can also scan local IP addresses on your network to find a few devices to narrow it down. ("Scan Subnet" button)

Please ensure you have connected using a Wireless or Wired ethernet connection for all devices you intend to use.
Once a user has connected to the server using a client device, they can send and receive text messages using the client device, which will text using the server device's SMS. This is done through the local ethernet connection that you are running on both your server and client device. 
