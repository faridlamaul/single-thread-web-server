import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleThread {
    public static void main(String[] args) throws Exception {
        try {
            String websiteRoot = "/home/faridlamaul/Project/Kuliah/Progjar/single-thread-web-server/root/";
            int port = 2022;
            String crlf = "\r\n";
            String contentType;
            String fileContent;
            String statusCode;
            String message;
            String urn;
            String fileExtension;
            FileInputStream fis;
            
            ServerSocket server = new ServerSocket(port);
            System.out.println("------- Server started in port " + port + " -------");
            Socket client = server.accept();
            System.out.println("------- Client connected -------");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            message = reader.readLine();
            urn = message.split(" ")[1];
            urn = urn.substring(1);

            // get file extension
            fileExtension = urn.substring(urn.lastIndexOf(".") + 1);

            if(fileExtension.equals("html") || fileExtension.equals("txt")) {
                contentType = "text/html";
                try {
                    fis = new FileInputStream(websiteRoot + urn);
                    fileContent = new String(fis.readAllBytes());
                    
                    statusCode = "200 OK";
                } catch (FileNotFoundException e) {
                    fileContent = "File not found";
                    statusCode = "404 Not Found";
                }
    
                output.write("HTTP/1.1 " + statusCode + crlf);
                output.write("Content-Type: " + contentType + crlf);
                output.write("Content-Length: " + fileContent.length() + crlf + crlf + fileContent);
                output.flush();
                output.close();

            } else { 
                // contentType = "application/octet-stream";
                try {
                    fis = new FileInputStream(websiteRoot + urn);
                    fileContent = new String(fis.readAllBytes());
                    
                    statusCode = "200 OK";
                } catch (FileNotFoundException e) {
                    fileContent = "File not found";
                    statusCode = "404 Not Found";
                    
                }
                
                output.write("HTTP/1.1 " + statusCode + crlf);
                output.write("Content-Type: text/html" + crlf);
                // output.write("Content-Type: " + contentType + crlf);
                output.write("Content-Length: " + fileContent.length() + crlf);
                output.write("Content-Disposition: inline" + crlf + crlf + fileContent);
                output.flush();
                output.close();
            }

            client.close();
            server.close();
        } catch (Exception e) {
            Logger.getLogger(SingleThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

