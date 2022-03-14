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
            // directory root 
            String websiteRoot = "/home/faridlamaul/Project/Kuliah/Progjar/single-thread-web-server/root/";
            
            // initialize variables
            int port = 2022;
            String crlf = "\r\n";
            String contentType;
            String fileContent;
            String statusCode;
            String message;
            String urn;
            String fileName;
            String fileExtension;
            FileInputStream fis;
            
            // create server socket
            ServerSocket server = new ServerSocket(port);
            System.out.println("- Server started in port " + port);

            // listen for client
            Socket client = server.accept();
            System.out.println("- Client connected");
            
            // create input and output streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            // read request
            message = reader.readLine();
            urn = message.split(" ")[1];
            urn = urn.substring(1);
            
            // get file name ex. file.pdf or file.html
            fileName = urn.substring(urn.lastIndexOf("/") + 1);

            // get file extension ex pdf or html
            fileExtension = urn.substring(urn.lastIndexOf(".") + 1);

            // get file content
            if(fileExtension.equals("html")) {
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
                output.write("Content-Length: " + fileContent.length() + crlf + crlf);
                output.write(fileContent);
                output.flush();

            } else { 
                contentType = "application/octet-stream";
                try {
                    fis = new FileInputStream(websiteRoot + urn);
                    fileContent = new String(fis.readAllBytes());
                    statusCode = "200 OK";

                    System.out.println("File found");
                    output.write("HTTP/1.1 " + statusCode + crlf);
                    output.write("Content-Type: " + contentType + crlf);
                    output.write("Content-Length: " + fileContent.length() + crlf);
                    output.write("Content-Disposition: attachment; filename=" + "\"" + fileName + "\"" + crlf + crlf);
                    output.flush();
                } catch (FileNotFoundException e) {
                    fileContent = "File not found";
                    statusCode = "404 Not Found";
                    
                    System.out.println("File not found");
                    output.write("HTTP/1.1 " + statusCode + crlf + crlf);
                    output.write(fileContent);
                    output.flush();
                }
            }

            client.close();
            server.close();
        } catch (Exception e) {
            Logger.getLogger(SingleThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

