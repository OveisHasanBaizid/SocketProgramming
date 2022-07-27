import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SimpleClient {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        //Get the port number to communicate with the server
        System.out.print("Enter port server : ");
        int port = scanner.nextInt();
        scanner.nextLine();
        //Establish input and output communication with the server
        Socket socket = new Socket("localhost", port);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        String ls = """
                * * * Menu * * *
                1.Get
                2.Put
                3.Ls
                4.Quit""";
        X:
        while (true) {
            //print list menu
            System.out.println(ls);
            /*
            Takes the command number and sends it to the server.
             At the output of the socket, the user writes that it is the input of the server socket
            */
            System.out.print("input> ");
            String strInput = scanner.nextLine();
            out.writeUTF(strInput);
            out.flush();
            //Checks the entered number with one use of a case switch
            switch (strInput) {
                //If the number entered is 1
                case "1" -> {
                    //Requests the file name from the user and sends it to the server
                    System.out.print("input file name> ");
                    String fileName = scanner.nextLine();
                    out.writeUTF(fileName);
                    out.flush();
                    //If the download message was received from the server. The file starts downloading
                    if (in.readUTF().equals("start download")) {
                        System.out.println("Downloading...");
                        FileOutputStream outputStream = new FileOutputStream(fileName);
                        byte[] bytes = new byte[1024];
                        while (true) {
                            in.read(bytes);
                            if (new String(bytes).contains("finish")) {
                                break;
                            }
                            outputStream.write(bytes);
                        }
                        outputStream.close();
                        System.out.println(fileName + " Downloaded.");
                    }
                    // Otherwise, the file message is not displayed
                    else {
                        System.out.println("File Not Exist.");
                    }
                }
                //If the number entered is 2
                case "2" -> {
                    //A file address is received from the user
                    System.out.print("input file path> ");
                    String filePath = scanner.nextLine();
                    File file = new File(filePath);
                    //If the file was available. The file is sent to the server
                    if (file.exists()) {
                        String[] nameFile = filePath.split(Pattern.quote(File.separator));
                        out.writeUTF(nameFile[nameFile.length - 1]);
                        FileInputStream fl = new FileInputStream(file);
                        System.out.println("uploading...");
                        int count;
                        byte[] buffer = new byte[1024];
                        while ((count = fl.read(buffer)) > 0) {
                            out.write(buffer, 0, count);
                        }
                        out.write("finish".getBytes());
                        out.flush();
                        System.out.println("File uploaded successfully.");
                    }
                    //Otherwise, the file message is not displayed
                    else {
                        System.out.println("File not exist");
                    }
                }
                //If the number entered is 3
                case "3" -> {
                    //A list of Jud files is requested on the server and prints the result to the output
                    System.out.println("* * * List Files * * *");
                    String listFiles = in.readUTF();
                    System.out.println(listFiles);
                }
                //If the number entered is 4 . The user is disconnected and the server is disconnected from the client
                case "4" -> {
                    System.out.println("Connection closed");
                    break X;
                }

                default -> System.out.println("Invalid input.");
            }
        }
    }
}