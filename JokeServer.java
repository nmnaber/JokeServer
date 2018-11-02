/*--------------------------------------------------------

1. Name / Date: Nick Naber

2. Java version used, if not the official version for the class:

build 1.8.0_152

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java

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

User state is stored in Arraylist in MAP with the key being a UUID passed from JokeClient. This was implemented using
the 'singleton' pattern to ensure there are not mulitple UserMaps. The state of the server is also store in a singleton class
and accessed before the logic to display a joke or proverb. Joke status is stored in ArrayList index 0-3 and proverb status
in 4-7. Before selecting a joke a random number is selected in the given range and checked againist the user state to ensure
that index is not occupied by a 1. A joke or proverb is then send back to the JokeClient.

----------------------------------------------------------*/

import java.io.*; // IO library import
import java.net.*; // Network library import
import java.util.*;
import java.util.Random;


class Worker extends Thread {
        Socket sock; //create a new Socket object

        Worker(Socket s) {
            sock = s;
        }

        public void run() {

            PrintStream out = null;
            BufferedReader in = null;

            try {
                in = new BufferedReader
                        (new InputStreamReader(sock.getInputStream())); //read and print Unicode characters over the socket

                out = new PrintStream(sock.getOutputStream());

                try {
                    String name;
                    String clientIDasString;


                    name = in.readLine(); //take input from BufferedReader 'in' , ex. IP address
                    clientIDasString = in.readLine();

                    System.out.println("New Connection from Client " + name + " , UUID: " +clientIDasString);


                    UserMap userStates = UserMap.getUserMap(); // pull hashmap of all users

                    //logic to check UUID aganist current users

                    if (userStates.findUser(clientIDasString) == true) {
                        System.out.println("Client " + name + " has been found! , UUID: " +clientIDasString);

                        }


                    //if new user is not found, create a new entry in UserMap

                    if (userStates.findUser(clientIDasString) == false) {
                        userStates.addUser(clientIDasString);
                        System.out.println("Client " + name + " has been added! , UUID: " +clientIDasString);

                    }

                    ArrayList reportState = userStates.pullUserState(clientIDasString);

                    //report client name, UUID, and user state prior to serving next joke or proverb

                    System.out.println("Client " + name + " is being served, UUID: " +clientIDasString + " Reported State: " +reportState.toString());

                    //use serverModeChange class to determine mode

                    serverModeChange serverMode = serverModeChange.getServerMode();

                    String currentMode = serverMode.serverModeGet();

                    //logic to pick mode

                    if (currentMode == "JOKE") {
                        pickJoke(name, clientIDasString, out, userStates); //send random joke to client
                    }

                    else if (currentMode == "PROVERB") {
                        pickProverb(name, clientIDasString, out, userStates); //send random proverb back to client
                    }




                } catch (IOException x) {
                    System.out.println("Server read error");
                    x.printStackTrace();
                }
                sock.close();

            } catch (IOException ioe) {
                System.out.println("Server read error");

            }

        }


        static void pickJoke (String name, String UUID, PrintStream out, UserMap userStates){

            //mapping JA to userState 0, JB to 1, etc...

            userStates.jokeStateReset(UUID);  //check if jokes need to be reset

            //get a random number between 0 and 3
            int random = (int)(Math.random()*((3-0)+1))+0;

            ArrayList<Integer> userState = new ArrayList<Integer>(userStates.pullUserState(UUID));

            //ensure a random selection

            int test = (int) userState.get(random);

            while (test == 1) {
                random = (int)(Math.random()*((3-0)+1))+0;
                test = (int) userState.get(random);
            }


            if (random == 0) {
                out.println ("JA: Hey " + name + " . How did the horse cross the river? .... A ferrari.  ");
                userStates.updateUserState( UUID, 0);
            }

            if (random == 1) {
                out.println ("JB: You see " + name + " failure should be as natural as breathing. Especially if you're asthmatic. ");
                userStates.updateUserState( UUID, 1);
            }

            if (random == 2) {
                out.println ("JC: Hey " + name + ". What do you call two Apple employees? ... Employee  ");
                userStates.updateUserState( UUID, 2);
            }

            if (random == 3) {
                out.println ("JD: Yes! That's right  " + name + "! <insert year> is the year of Linux on the desktop!");
                userStates.updateUserState( UUID, 3);
            }

        }

    static void pickProverb (String name, String UUID, PrintStream out, UserMap userStates){

        //mapping JA to userState 0, JB to 1, etc...

        userStates.proverbStateReset(UUID);  //check if proverbs need to be reset

        //get a random number between 4 and 7

        Random rand = new Random();

        int random = rand.nextInt((7-4)+1)+4;

        ArrayList<Integer> userState = new ArrayList<Integer>(userStates.pullUserState(UUID));

        //ensure a random selection

        int test = (int) userState.get(random);

        while (test == 1) {
            random = rand.nextInt((7-4)+1)+4;
            test = (int) userState.get(random);
        }


        if (random == 4) {
            out.println ("PA: "+ name + ". Sell a person an Application and they will pay once, but sell a person a Subscription and they will pay for a lifetime. ");
            userStates.updateUserState( UUID, 4);
        }

        if (random == 5) {
            out.println ("PB: "+ name + ". A person who owns every Platform is beholden to no Ecosystem. ");
            userStates.updateUserState( UUID, 5);
        }

        if (random == 6) {
            out.println ("PC: " + name + ". A bad programmer always blames their IDE. ");
            userStates.updateUserState( UUID, 6);
        }

        if (random == 7) {
            out.println ("PD: " + name + ". No Null Pointer is a Good Pointer. ");
            userStates.updateUserState( UUID, 7);
        }



    }

        static String toText (byte ip[]) {

            StringBuffer result = new StringBuffer();

            for (int i = 0; i < ip.length; ++ i) {

                if (i > 0) result.append(".");
                result.append (0xff & ip[i]);
            }
            return result.toString();

        }
    }

        public class JokeServer {

            public static void main(String a[]) throws IOException {

                int q_len = 6; // maximum requests to que
                int port = 5555; // port to listen
                Socket sock;

                ServerSocket servsock = new ServerSocket(port, q_len); //create a new ServerSocket using the spec max reqs and port

                System.out.println(
                        "Nick Naber's Joke Server starting up, listening at port: " + port + " \n");

                AdminServer admin = new AdminServer(); //create a new AdminServer

                Thread adminThread = new Thread( admin ); //put the admin server on it's own thread

                adminThread.start();

                while (true) {

                    sock = servsock.accept(); //listens/accepts client connections
                    new Worker(sock).start(); //start Worker process



                }
            }


        }

class AdminServer implements Runnable{

    public static void main(String a[]){

        int adminport = 6666;

        System.out.println(
                "Nick Naber's AdminServer starting up, listening at port: " + adminport + " \n"); }

    public void run() {

        int q_len = 6; // maximum requests to que

        int adminport = 6666;

        try {

            Socket adminsock;

            ServerSocket adminservsock = new ServerSocket(adminport, q_len) ; //create a new ServerSocket using the spec max reqs and port

            while (true) {

                adminsock = adminservsock.accept(); //listens/accepts client connections
                new AdminWorker(adminsock).start();//start Worker process

            }

        }

        catch (IOException x){

            System.out.println(
                    "Admin server error - exiting."); }

    }
            //TODO exit code

}

class AdminWorker extends Thread {
    Socket sock; //create a new Socket object

    AdminWorker(Socket s) {
        sock = s;
    }

    public void run() {

        PrintStream out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader
                    (new InputStreamReader(sock.getInputStream())); //read and print Unicode characters over the socket

            out = new PrintStream(sock.getOutputStream());

            try {

                String tester = in.readLine();

                serverModeChange serverModeBefore = serverModeChange.getServerMode();

                String currentMode = serverModeBefore.serverModeGet();

                System.out.println("Mode is currently " + currentMode + " from...   " + tester);

                serverModeChange serverMode = serverModeChange.getServerMode();

                serverMode.changeServerMode();

                String afterMode = serverMode.serverModeGet();

                System.out.println("Mode is changed to " + afterMode + " from...   " + tester);

                out.println("You just changed the mode to " + afterMode );

            } catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace();
            }
            sock.close();

        } catch (IOException ioe) {
            System.out.println("Server read error");

        }

    }

}

class serverModeChange {

    private static serverModeChange single_instance = null;

    private Boolean ServerMode;

    private serverModeChange() {

        ServerMode = true; //true is joke

    }

    public static serverModeChange getServerMode() {

        if (single_instance == null)
            single_instance = new serverModeChange();

        return single_instance;
    }

    public void changeServerMode() {

        if (ServerMode == true) {
            ServerMode = false;
        }

        else if (ServerMode == false) {
            ServerMode = true;
        }

    }

    public String serverModeGet() {

        if (ServerMode == true) { return "JOKE"; }
        else { return "PROVERB"; }

    }

}

class UserMap {

    private static UserMap single_instance = null;

    private Map<String, ArrayList<Integer>> userMap;

    private UserMap() {

        userMap = new HashMap< String, ArrayList<Integer> >();

    }

    public static UserMap getUserMap() {

        if (single_instance == null)
            single_instance = new UserMap();

        return single_instance;
    }

    public void addUser(String newUser) {

        userMap.put(newUser, new ArrayList<Integer>(10));

        ArrayList<Integer> test = userMap.get(newUser);

        test.add(0);
        test.add(0);
        test.add(0);
        test.add(0);
        test.add(0);
        test.add(0);
        test.add(0);
        test.add(0);
    }

    public ArrayList<Integer> pullUserState(String usertoFind) {

        ArrayList<Integer> foundUserState = userMap.get(usertoFind);

        return foundUserState;
    }

    public boolean findUser(String usertoFind) {

        boolean foundUser =  userMap.containsKey(usertoFind);

        return foundUser;
    }

    public void updateUserState (String usertoFind, Integer index){

        ArrayList<Integer> foundUserState = userMap.get(usertoFind);

        foundUserState.set(index, 1);

        userMap.replace(usertoFind, foundUserState);

    }

    public void jokeStateReset (String usertoFind){

        ArrayList<Integer> foundUserState = userMap.get(usertoFind);
        //test 0-3

        int resetCount = 0;

        int arrayValue = 0;

        for ( int test = 0; test < 4 ; test++) {

            arrayValue = (int) foundUserState.get(test);

            if (arrayValue == 1)
                resetCount++;

        }

        //reset 0-3

        if (resetCount == 4) {
            foundUserState.set(0,0);
            foundUserState.set(1,0);
            foundUserState.set(2,0);
            foundUserState.set(3,0);

        }

        userMap.replace(usertoFind, foundUserState);


    }

    public void proverbStateReset (String usertoFind){

        ArrayList<Integer> foundUserState = userMap.get(usertoFind);
        //test 4-7

        int resetCount = 0;

        int arrayValue = 0;

        for ( int test = 4; test < 8 ; test++) {

            arrayValue = (int) foundUserState.get(test);

            if (arrayValue == 1)
                resetCount++;

        }

        //reset 0-3

        if (resetCount == 4) {
            foundUserState.set(4,0);
            foundUserState.set(5,0);
            foundUserState.set(6,0);
            foundUserState.set(7,0);

        }

        userMap.replace(usertoFind, foundUserState);


    }


}











