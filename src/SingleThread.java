import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            String fileName;
            FileInputStream fis;
            ServerSocket server = new ServerSocket(port);
            while(true) {
                System.out.println("------- Server started in port " + port + " -------");
                Socket client = server.accept();
                System.out.println("------- Client connected -------");
                
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                message = br.readLine();
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
    
                    while (!message.isEmpty()) {
                        message = br.readLine();
                    }
        
                    bw.write("HTTP/1.1 " + statusCode + crlf);
                    bw.write("Content-Type: " + contentType + crlf);
                    bw.write("Content-Length: " + fileContent.length() + crlf + crlf + fileContent);
                    bw.flush();
                    bw.close();
                } else { 
                    contentType = "application/octet-stream";

                    try {
                        fis = new FileInputStream(websiteRoot + urn);
                        fileContent = "This file is being downloaded (" + urn + ")";
                        
                        statusCode = "200 OK";
                    } catch (FileNotFoundException e) {
                        fileContent = "File not found";
                        statusCode = "404 Not Found";
                        
                    }
    
                    while (!message.isEmpty()) {
                        message = br.readLine();
                    }
        
                    bw.write("HTTP/1.1 " + statusCode + crlf);
                    System.out.println(statusCode);
                    bw.write("Content-Type: " + contentType + crlf);
                    System.out.println(contentType);
                    bw.write("Content-Length: " + fileContent.length() + crlf + crlf + fileContent);
                    // bw.write("Content-Disposition: attachment; filename=" + urn + crlf + crlf);
                    System.out.println(urn);
                    bw.flush();
                    bw.close();
                }
                
                
                // try {
                //     fis = new FileInputStream(websiteRoot + urn);
                //     fileContent = new String(fis.readAllBytes());
                    
                //     statusCode = "200 OK";
                // } catch (FileNotFoundException e) {
                //     fileContent = "File not found";
                //     statusCode = "404 Not Found";
                // }

                // while (!message.isEmpty()) {
                //     // System.out.println(message);
                //     message = br.readLine();
                // }
    
                // bw.write("HTTP/1.1 " + statusCode + crlf + "Content-Type: " + contentType + crlf + "Content-Length: " + fileContent.length() + crlf + crlf + fileContent);
                // bw.flush();
    
                client.close();
            }
                // server.close();
        } catch (Exception e) {
            Logger.getLogger(SingleThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

