import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Dora Di
 * <p>
 * 1. Create a server socket and bind it to a specific port number
 * 2. Listen for a connection from the client and accept it. This results in a client socket, created on the server, for the connection.
 * 3. Read data from the client via an InputStream obtained from the client socket
 * 4. Send data to the client via the client socket’s OutputStream.
 * 5. Close the connection with the client.
 * <p>
 * The steps 3 and 4 can be repeated many times depending on the protocol agreed between the server and the client.
 */

public class TCPS {
    public static final int PORT = 6666;
    public static ServerSocket serverSocket = null; // Server gets found
    public static Socket openSocket = null;         // Server communicates with the client

    public static void configureServer() throws UnknownHostException, IOException {
        // get server's own IP address
        String serverIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Server ip: " + serverIP);
        // create a socket at the predefined port
        serverSocket = new ServerSocket(PORT);
    }

    public static void connectClient(Socket openSocket, int clientNumber) throws IOException {
        String request, response;

        // two I/O streams attached to the server socket:
        Scanner in;         // Scanner is the incoming stream (requests from a client)
        PrintWriter out;    // PrintWriter is the outcoming stream (the response of the server)
        in = new Scanner(openSocket.getInputStream());
        out = new PrintWriter(openSocket.getOutputStream(), true);
        // Parameter true ensures automatic flushing of the output buffer

        // Server keeps listening for request and reading data from the Client,
        // until the Client sends "stop" requests
        while (in.hasNextLine()) {
            request = in.nextLine();
            if (request.equals("stop")) {
                out.println("Good bye, client!");
                System.out.println("Log: " + request + " client!");
                break;
            } else {
                // server responses
                response = new StringBuilder(request).reverse().toString();
                out.println(response);
                // Log response on the server's console, too
                System.out.println("Client nr " + clientNumber + ": " + response);
            }
        }
    }

    public static void rcvThread(Socket sock, int clientNumber) {
        Thread recieveThread = new Thread(() -> {
            try {
                connectClient(sock, clientNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    openSocket.close();
                    System.out.println("Connection to client closed");
                    serverSocket.close();
                    System.out.println("Server port closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        recieveThread.start();
    }

    public static void main(String[] args) throws IOException {
        configureServer();
        int clientNumber = 0;
        while (true) {
            try {
                openSocket = serverSocket.accept();
                System.out.println("Server accepts requests at: " + openSocket);
                clientNumber++;
                rcvThread(openSocket, clientNumber);

            } catch (Exception e) {
                System.out.println(" Connection fails: " + e);
            }
        }
    }
}