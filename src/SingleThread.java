import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
            String directoryPath;
            FileInputStream fis;
            Path path;
            String mimeType;
            
            // create server socket
            ServerSocket server = new ServerSocket(port);
            System.out.println("***** Server started in port " + port + " *****");

            // listen for client
            Socket client = server.accept();
            System.out.println("***** Client connected *****");
            
            // create input and output streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            // read request
            message = reader.readLine();
            urn = message.split(" ")[1];
            System.out.println(urn);
            urn = urn.substring(1);
            System.out.println(urn);

            // get file name ex. file.pdf or file.html
            fileName = urn.substring(urn.lastIndexOf("/") + 1);
            
            // get content type ex. text/html
            path = new File(fileName).toPath();
            mimeType = Files.probeContentType(path);
            contentType = mimeType;

            // get path of file
            directoryPath = websiteRoot + urn;

            // request is a directory or file
            if (urn.contains(".")) {

                // check if file is html or not 
                if(contentType.equals("text/html")) {
                    try {
                        fis = new FileInputStream(directoryPath);
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
                    try {
                        fis = new FileInputStream(directoryPath);
                        fileContent = new String(fis.readAllBytes());

                        statusCode = "200 OK";

                        System.out.println("File found");
                        output.write("HTTP/1.1 " + statusCode + crlf);
                        output.write("Content-Type: " + contentType + crlf);
                        output.write("Content-Length: " + fileContent.length() + crlf);
                        output.write("Content-Disposition: attachment; filename=" + "\"" + fileName + "\"" + crlf + crlf);
                        output.write(fileContent);
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
            } else {
                // get directory and file name when requests a directory that does not have index.html inside it
                listDirectory(directoryPath);
                System.out.println(directoryPath);
                
            }

            client.close();
            server.close();
        } catch (Exception e) {
            Logger.getLogger(SingleThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void listDirectory(String path) {
        File directoryPath = new File(path);

        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        System.out.println("List of files and directories in the specified directory:");
        for(File file : filesList) {
            System.out.println("File name: " + file.getName());
            System.out.println("File path: " + file.getPath());
            System.out.println("Last Modified Date: " + new java.util.Date(file.lastModified()));
            System.out.println("Size :" + file.length()+" bytes");
            System.out.println(" ");
        }
    }
}

