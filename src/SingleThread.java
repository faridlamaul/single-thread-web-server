import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
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
                File dirPath = new File(directoryPath);
                
                //List of all files and directories
                File files[] = dirPath.listFiles();
                if (files != null) {
                    String fileContent2 = "";
                    fileContent2 += "<html>\r\n" + "<body>";

                    for (File file : files) {
                        contentType = Files.probeContentType(new File(file.getName()).toPath());
                        fileContent = file.getName();
                        if (contentType.equals("text/html")) {
                            fis = new FileInputStream(file);
                            fileContent = new String(fis.readAllBytes());

                            statusCode = "200 OK";
                            
                            output.write("HTTP/1.1 " + statusCode + crlf);
                            output.write("Content-Type: " + contentType + crlf);
                            output.write("Content-Length: " + fileContent.length() + crlf + crlf);
                            output.write(fileContent);
                            output.flush();
                        } else {
                            fileContent2 += "<a href=\"" + file.getPath() + "\">" + file.getName() + "</a><br>";
                        }
                    }

                    fileContent2 += "</body>\r\n" + "</html>";
                    System.out.println(fileContent2);
                    output.write("HTTP/1.1 200 OK" + crlf + crlf);
                    output.write(fileContent2);
                    output.flush();
                } else {
                    fileContent = "Directory not found";
                    statusCode = "404 Not Found";
                    
                    System.out.println("Directory not found");
                    output.write("HTTP/1.1 " + statusCode + crlf + crlf);
                    output.write(fileContent);
                    output.flush();
                }

                // for(File file : files) {
                //     if (file.getName().equals("index.html")) {
                //         try {
                //             fis = new FileInputStream(directoryPath);
                //             fileContent = new String(fis.readAllBytes());
                            
                //             statusCode = "200 OK";
                //         } catch (FileNotFoundException e) {
                //             fileContent = "File not found";
                //             statusCode = "404 Not Found";
                //         }
            
                //         output.write("HTTP/1.1 " + statusCode + crlf);
                //         output.write("Content-Type: " + contentType + crlf);
                //         output.write("Content-Length: " + fileContent.length() + crlf + crlf);
                //         output.write(fileContent);
                //         output.flush();
                //     } else {
                //         try {
                //             fileContent = new String(Files.readAllBytes(file.toPath()));
                //             System.out.println(fileContent);
                //             statusCode = "200 OK";
                //             System.out.println("File found");
                        
                //             output.write("HTTP/1.1 " + statusCode + crlf);
                //             output.write("Content-Type: " + contentType + crlf);
                //             output.write("Content-Length: " + fileContent.length() + crlf + crlf);

                //             output.write(
                //                 "<html><body><h2>" + file.getName() + "<h2></h2></html>" + crlf + crlf
                //             );
                //             output.flush();
                //         } catch (Exception e) {
                //             fileContent = "File not found";
                //             statusCode = "404 Not Found";
                            
                //             System.out.println("File not found");
                //             output.write("HTTP/1.1 " + statusCode + crlf + crlf);
                //             output.write(fileContent);
                //             output.flush();
                //         }
                //     }
                //     System.out.println("File name: " + file.getName());
                //     System.out.println("File path: " + file.getPath());
                //     System.out.println("Last Modified Date: " + new java.util.Date(file.lastModified()));
                //     System.out.println("Size :" + file.length() + " bytes");
                //     System.out.println(" ");
                // }
                
            }

            client.close();
            server.close();
        } catch (Exception e) {
            Logger.getLogger(SingleThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    // public static void listDirectory(String path) {
    //     File dirPath = new File(path);

    //     //List of all files and directories
    //     File files[] = dirPath.listFiles();
    //     System.out.println("List of files and directories in the specified directory:");
    //     for(File file : files) {
    //         System.out.println("File name: " + file.getName());
    //         System.out.println("File path: " + file.getPath());
    //         System.out.println("Last Modified Date: " + new java.util.Date(file.lastModified()));
    //         System.out.println("Size :" + file.length()+" bytes");
    //         System.out.println(" ");
    //     }
    // }
}

