import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleSocketServer extends Thread {
    private ServerSocket serverSocket;
    private final int port;
    private boolean running = false;

    public SimpleSocketServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                System.out.println("Listening for a connection");

                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestServerHandler requestHandler = new RequestServerHandler(socket);
                requestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter port : ");
        int port = input.nextInt();
        System.out.println("Start server on port: " + port);

        SimpleSocketServer server = new SimpleSocketServer(port);
        server.startServer();

        // Automatically shutdown in 2 hour

        try {
            Thread.sleep(1200000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.stopServer();
    }
}

