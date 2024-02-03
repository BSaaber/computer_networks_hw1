import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static final int PORT = 12332;
    public static void main(String[] args) throws IOException {
        String s = new File("").getAbsolutePath() + "/new_file.xlsx";

        System.out.println(s);

    }

}