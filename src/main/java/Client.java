import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.Socket;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Client {

    public static final List<NMQ> nmqConfigs = List.of(new NMQ(32, 145, 10));

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("[DEV] Client has been started!");

        Socket socket = new Socket("127.0.0.1", 12332); // todo - move to cli arguments
        socket.setTcpNoDelay(true);
        OutputStream requestWriter = socket.getOutputStream();
        Scanner responseListener = new Scanner(socket.getInputStream());

        ArrayList<ArrayList<GraphDot>> results = new ArrayList<>();

        for (int i = 0; i < nmqConfigs.size(); i++) {
            NMQ nmqConfig = nmqConfigs.get(i);
            System.out.println("Start working with configuration nmq " + nmqConfig.N + " " + nmqConfig.M + " " + nmqConfig.Q);
            ArrayList<GraphDot> configResults = new ArrayList<>();
            for (int k = 0; k < nmqConfig.M; ++k) {
                int byteAmount = nmqConfig.N * k + 8;
                ArrayList<Long> iterationResults = new ArrayList<>();

                for (int q = 0; q < nmqConfig.Q; ++q) {
                    byte[] randomBytesArray = new byte[byteAmount + 1];
                    new Random().nextBytes(randomBytesArray);
                    randomBytesArray[randomBytesArray.length - 1] = '\n';

                    long start = System.nanoTime() / 1000; // todo rename milli to micro

                    requestWriter.write(randomBytesArray);
                    requestWriter.flush();
                    String responseFromServer = responseListener.nextLine();

                    long end = System.nanoTime() / 1000;


                    iterationResults.add( (end - start) / nmqConfig.Q );
                }

//                System.out.println("Ended " + k + "th iteration for bytes size " + byteAmount);

                Long mediumTime = 0L;
                for (Long iterationResult : iterationResults) {
                    mediumTime += iterationResult;
                }

                configResults.add(new GraphDot(mediumTime, byteAmount));

//                System.out.println("Medium is " + mediumTime);

            }
            results.add(configResults);

        }

        requestWriter.close();
        responseListener.close();
        socket.close();



        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Results");
        sheet.setColumnWidth(0, 12000);
        sheet.setColumnWidth(1, 12000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Time (mills)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Amount of bytes");
        headerCell.setCellStyle(headerStyle);

        ArrayList<GraphDot> configResults = results.getFirst();
        for (int i = 0; i < configResults.size(); ++i) {
            Row row = sheet.createRow(i + 1);

            Cell timeCell = row.createCell(0);
            timeCell.setCellValue(configResults.get(i).timeInMills);

            Cell bytesAmountCell = row.createCell(1);
            bytesAmountCell.setCellValue(configResults.get(i).bytesAmount);
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "results.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();

    }
}
