import java.io.*;
import java.net.Socket;
import java.net.SocketException;
class RequestServerHandler extends Thread {
    private final Socket socket;
    RequestServerHandler(Socket socket) {
        this.socket = socket;
    }
   
    @Override
    public void run() {
        try {
            System.out.println("Received a connection");

            // Get input and output stream
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // Echo lines back to the client until the client closes the connection
            String line;
            X:
            while (true) {
                //Reads a line as a command type from the socket input
                try {
                    line = in.readUTF();
                } catch (SocketException e) {
                    break;
                }
                //Checks the type of command sent
                switch (line) {
                    /*
                    If the command entered is 1, then the user has a request to download a file.
                     The name of a file is read from the socket input and is searched in the server memory.
                     If the file exists, it sends the file to the user,
                     otherwise it sends the message "File not available" to the user.
                    */
                    case "1" -> {
                        String name = in.readUTF();
                        String pathFile = "D:\\Data\\" + name;
                        File file = new File(pathFile);
                        if (file.exists()) {
                            out.writeUTF("start download");
                            out.flush();
                            FileInputStream fl = new FileInputStream(file);
                            int count;
                            byte[] buffer = new byte[1024];
                            while ((count = fl.read(buffer)) > 0) {
                                out.write(buffer, 0, count);
                            }
                            out.write("finish".getBytes());
                            out.flush();
                        } else {
                            out.writeUTF("file not exist");
                            out.flush();
                        }
                    }
                    /*
                    If the command entered was 2.
                     So the user requests to upload a file.
                     The file name is received from the user and then
                     the download of the file from the user begins. The file is stored in the server memory.
                    */
                    case "2" -> {
                        String nameFile = in.readUTF();
                        FileOutputStream outputStream = new FileOutputStream("D:\\Data\\" + nameFile);
                        byte[] bytes = new byte[1024];
                        while (true) {
                            in.read(bytes);
                            if (new String(bytes).contains("finish")) {
                                break;
                            }
                            outputStream.write(bytes);
                        }
                        outputStream.close();
                        System.out.println(nameFile + " uploaded successfully.");
                    }
                    /*
                    If the command entered was 3.
                    The user then requests a list of files in the server
                    memory and a list of files is sent to the user.
                    */
                    case "3" -> {
                        String[] list = new File("D:\\Data\\").list();
                        StringBuilder listFiles = new StringBuilder();
                        if (list != null) for (int i = 0; i < list.length; i++) {
                            listFiles.append(i + 1).append(" -> ").append(list[i]).append("\n");
                        }
                        out.writeUTF(listFiles.toString());
                        out.flush();
                    }
                    /*
                    If the number entered on the user side was 4,
                     it means that the user has left the socket
                     and its connection with the server has been disconnected,
                     then the server exits the loop and closes the connection.
                    */
                    case "4" -> {
                        break X;
                    }
                    default -> System.out.println("Invalid input.");
                }
            }

            // Close our connection
            in.close();
            out.close();
            socket.close();

            System.out.println("Connection closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
