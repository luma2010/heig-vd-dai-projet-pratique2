package ch.heigvd.dai;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 4444;
        try(ServerSocket server = new ServerSocket(port)) {
            System.out.println("Waiting for client...");
            Socket socket = server.accept();
            System.out.println("Client connected");
            OutputStreamWriter oos = new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8);
            BufferedWriter wr = new BufferedWriter(oos);
            InputStreamReader ios = new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(ios);
            wr.write("Welcome to DAI project 2\n");
            wr.flush();
            while (true) {
                wr.write("Please select one of the following :\nLS\nCAT <filename>\nVARIABLES <filename>\nEXIT \n");
                wr.flush();
                String message = br.readLine();
                System.out.println(message);

                if(message.startsWith("EXIT")) {
                    ios.close();
                    break;
                } else if(message.startsWith("LS")) {
                    wr.write("LS SELECTED\n");
                    wr.flush();
                } else if(message.startsWith("CAT")) {
                    if(message.length() <= 4) {
                        wr.write("ERROR 3\n");
                        wr.flush();
                    } else {
                        String filename = message.substring(4);
                        InputStream is = new FileInputStream(filename);
                        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                        BufferedReader cat = new BufferedReader(reader);
                        String line;
                        while ((line = cat.readLine()) != null) {
                            wr.write(line + "\n");
                            wr.flush();
                        }
                        wr.write("|||\n");
                        wr.flush();
                    }
                } else if(message.startsWith("VARIABLES")) {
                    if(message.length() <= 10) {
                        wr.write("ERROR 3\n");
                    } else {
                        String filename = message.substring(10);

                        wr.write(" Please Select one of the following :\nADD <varName> <varValue> \nDELETE <varName> \nMODIFY <varName> <varValue> \nRENAME <varName> <newVarName>\n");
                        wr.flush();
                        message = br.readLine();
                        String[] token = message.split(" ");
                        if(token[0].startsWith("ADD")) {
                            Add.addVariableToFile(filename, token[1],token[2]);
                            wr.write("Variable created\n");
                            wr.flush();
                        }else if(token[0].startsWith("DELETE")) {
                            Delete.deleteVariableToFile(filename, token[1]);
                            wr.write("Variable delete\n");
                            wr.flush();
                        }else if(token[0].startsWith("MODIFY")) {
                            Modify.modifyVar(filename, token[1], token[2]);
                            wr.write("Variable name modified\n");
                            wr.flush();
                        }else if(token[0].startsWith("RENAME")) {
                            Rename.renameVar(filename, token[1], token[2]);
                            wr.write("Variable value modified\n");
                            wr.flush();
                        }else{
                            wr.write("ERROR\n");
                            wr.flush();
                        }
                    }
                } else {
                    wr.write("MESSAGE OUT \n");
                    wr.flush();
                }
            }
            oos.close();
            socket.close();
            System.out.println("Closing server");
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
