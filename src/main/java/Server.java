import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) throws IOException {
        System.out.println("[DEV] Server has been started!");

        int port = 0;
        for (int i = 0; i < args.length; ++i) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        Scanner requestListener = new Scanner(socket.getInputStream());
        PrintWriter replier = new PrintWriter(socket.getOutputStream());

        System.out.println("[DEV] Client connected");

        while (true) {
            String textFromClient = requestListener.nextLine();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            replier.println(dateFormat.format(date));
            replier.flush();
        }

//        requestListener.close();
//        replier.close();
//        socket.close();
//        serverSocket.close();

    }
}
