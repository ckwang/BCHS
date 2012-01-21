package alwayscall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple example pokerbot, in java. This is an example of a bare bones,
 * dumb pokerbot - it only sets up the socket necessary to connect with the 
 * engine and then always returns the same action. It is meant as an example of 
 * how a pokerbot should communicate with the engine.
 * @author oderby
 *
 */
public class Player {
	public static void main(String[] args){
	    // Socket which will connect to the engine.
        Socket socket = null;
        // Reader to read packets from engine
        BufferedReader inStream = null;
        // Convenience wrapper to write back to engine
        PrintWriter outStream = null;
        // port number specified by engine to connect to.
        int port = Integer.parseInt(args[0]);

        try {
            socket = new Socket("localhost", port);
            outStream = new PrintWriter(socket.getOutputStream(), true);
            inStream = new BufferedReader(new InputStreamReader(
                                        socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host - can't connect.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            System.exit(1);
        }
        
        String input;
        while (true) {
            try {
                // block until engine sends us a packet, then read it into input
                input = inStream.readLine();
            } catch (IOException e) {
                System.out.println("Connection reset while waiting to read.");
                e.printStackTrace();
                break;
            }
            if (input == null) {
                System.out.println("Gameover, engine disconnected");
                break;
            }
            // Here is where you should implement code to parse the packets from
            // the engine and act on it.
            System.out.println(input);
            if (input.split(" ")[0].compareToIgnoreCase("GETACTION") == 0) {
                // When appropriate, reply to the engine with a legal action.
                // The engine will ignore all spurious packets you send.
                // The engine will also check/fold for you if you return an
                // illegal action.
                outStream.println("CHECK");
            }
        }
        
        // Once the server disconnects from us, close our streams and sockets.
        try {
            outStream.close();
            inStream.close();
            socket.close(); 
        } catch (IOException e) {
            System.out.println("Encounterd problem shutting down connections");
            e.printStackTrace();
        }
	}
}
