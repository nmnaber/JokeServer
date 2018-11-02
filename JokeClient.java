/*--------------------------------------------------------

1. Name / Date: Nick Naber

2. Java version used, if not the official version for the class:

build 1.8.0_152

3. Precise command-line compilation examples / instructions:

> javac JokeClient.java

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

This client generates the UUID that is used as the key for the hashmap in the UserMap on the JokeServer.
Press enter to get a joke or proverb.

----------------------------------------------------------*/

import java.io.*;
import java.net.*;
import java.util.UUID;


public class JokeClient {

    public static void main(String args[]) {
        String serverName;
        String name = null;
        String enter = null;
        UUID clientID = UUID.randomUUID();

        if (args.length < 1) serverName = "localhost"; // if started without args default to localhost
        else serverName = args[0];



        System.out.println("Nick Naber's Joke Client. \n");
        System.out.println("Using server: " + serverName + ", Port: 5555");
        System.out.println("Enter your name to get a joke or proverb! ");


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            name = in.readLine(); //store into String Name the user input
        }
        catch (IOException x) {
            x.printStackTrace();
        }

        try {
            while (enter != "quit") {
                System.out.print("Press Enter to get a joke or proverb! Or (quit) to end : ");
                System.out.flush();
                enter = in.readLine();
                getJoke(name, serverName, clientID);
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

    static void getJoke (String name, String serverName, UUID clientID) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;

        try {

            sock = new Socket(serverName, 5555);


            fromServer=
                    new BufferedReader(new InputStreamReader(sock.getInputStream())); //read stream sent through socket from server
            toServer =
                    new PrintStream(sock.getOutputStream()); //send user input host or IP to server

            toServer.println(name); toServer.flush();
            toServer.println(clientID); toServer.flush();


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


