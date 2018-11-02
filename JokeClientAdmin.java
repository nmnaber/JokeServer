/*--------------------------------------------------------

1. Name / Date: Nick Naber

2. Java version used, if not the official version for the class:

build 1.8.0_152

3. Precise command-line compilation examples / instructions:

> javac JokeClientAdmin.java

4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

This is largely the same as the Joke Client. All of the work is server-side so no special functionality was added.
Press enter to change server mode. The mode is reported back from the server.

----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.UUID;


public class JokeClientAdmin {

    public static void main(String args[]) {
        String serverName;
        String enter = null;
        String message = "Message from JokeClientAdmin";

        if (args.length < 1) serverName = "localhost"; // if started without args default to localhost
        else serverName = args[0];



        System.out.println("Nick Naber's Admin Client. \n");
        System.out.println("Using server: " + serverName + ", Port: 6666");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


        try {
            while (enter != "quit") {
                System.out.print("Press Enter to change the mode of the server Or (quit) to end : ");
                System.out.flush();
                enter = in.readLine();
                modeChanger ( serverName, message);
            }

            System.out.println("Cancelled by user request.");

        }
        catch (IOException x) {
            x.printStackTrace();
        }

        }


    static String toText(byte ip[]) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < ip.length; i++) {
            if (i>0) result.append(".");
            result.append(0xff & ip[i]);
        }
        return result.toString();
    }

    static void modeChanger (String serverName, String message) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;

        try {

            sock = new Socket(serverName, 6666);


            fromServer=
                    new BufferedReader(new InputStreamReader(sock.getInputStream())); //read stream sent through socket from server
            toServer =
                    new PrintStream(sock.getOutputStream()); //send user input host or IP to server


            toServer.println(message); toServer.flush();


            for (int i = 1; i <= 3; i++) {
                textFromServer = fromServer.readLine();
                if (textFromServer != null) System.out.println(textFromServer);
            }

            sock.close();

        } catch (IOException x) {
            System.out.println ("Socket error.");
            x.printStackTrace();
        }
    }
}


