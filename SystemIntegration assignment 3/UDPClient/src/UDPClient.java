import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author Dora Di
 */
public class UDPClient {
    private static final int serverPort = 7777;

    // buffers for the messages
    public static String message;
    private static byte[] dataIn = new byte[10000];
    private static byte[] dataOut = new byte[10000];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket clientSocket;

        public static void main(String[] args) throws IOException {
            // Enter server's IP address as a parameter from Run/Edit Configuration/Application/Program Arguments
            clientSocket = new DatagramSocket();
            InetAddress serverIP = InetAddress.getByName(args[0]);
            System.out.println(serverIP);

            Scanner scan = new Scanner(System.in);
            System.out.println("Type file path to send image: ");

            while ((message = scan.nextLine()) != null) {
                try {
                    sendRequest(serverIP);
                    receiveResponse();
                } catch (IIOException e) {
                    System.out.println("Could not find image at path " + message + " please enter a different path");
                }
            }
            clientSocket.close();
        }

        public static void sendRequest(InetAddress serverIP) throws IOException {
            BufferedImage imageToSend = ImageIO.read(new File(message));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(imageToSend, "jpg", byteArrayOutputStream);
            dataOut = byteArrayOutputStream.toByteArray();
            requestPacket = new DatagramPacket(dataOut, dataOut.length, serverIP, serverPort);
            clientSocket.send(requestPacket);

        }

    public static void receiveResponse() throws IOException {
        responsePacket = new DatagramPacket(dataIn, dataIn.length);
        clientSocket.receive(responsePacket);
        String message = new String(responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println("Response from Server: " + message);
    }
}
