import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * @author Dora
 */
public class UDPServer {
    private static final int serverPort = 7777;

    // buffers for the messages
    private static byte[] dataIn = new byte[10000];
    private static byte[] dataOut = new byte[10000];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket serverSocket;


    public static void main(String[] args) throws Exception {
        String messageIn, messageOut;
        try {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            // Opens socket for accepting requests
            serverSocket = new DatagramSocket(serverPort);
            int numberOfFiles = 1;
            while (true) {
                System.out.println("Server " + serverIP + " running ...");
                messageIn = receiveRequest(numberOfFiles);
                numberOfFiles++;
                if (messageIn.equals("stop")) break;
                messageOut = processRequest(messageIn);
                sendResponse(messageOut);
            }
        } catch (Exception e) {
            System.out.println(" Connection fails: " + e);
        } finally {
            serverSocket.close();
            System.out.println("Server port closed");
        }
    }

    public static String receiveRequest(int fileNumber) throws IOException {
        requestPacket = new DatagramPacket(dataIn, dataIn.length);
        serverSocket.receive(requestPacket);
        File outputfile = new File("image" + fileNumber + ".jpg");
        FileOutputStream fos = new FileOutputStream(outputfile);
        try {
            fos.write(dataIn);
            System.out.println("Image data recieved and saved to file with name: " + outputfile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputfile.getName();
    }

    public static String processRequest(String fileName) {
        return fileName.toUpperCase();
    }

    public static void sendResponse(String fileName) throws IOException {
        InetAddress clientIP;
        int clientPort;

        clientIP = requestPacket.getAddress();
        clientPort = requestPacket.getPort();
        System.out.println("Client port: " + clientPort);
        System.out.println("Recieved file from Client");
        fileName = "Image was recieved and saved as file: " + fileName;
        dataOut = fileName.getBytes();
        responsePacket = new DatagramPacket(dataOut, dataOut.length, clientIP, clientPort);
        serverSocket.send(responsePacket);
        System.out.println("Message sent back: " + fileName);
    }
}
