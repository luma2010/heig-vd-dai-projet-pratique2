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
        ServerSocket server = new ServerSocket(port);
        System.out.println("Waiting for client...");
        while(!server.isClosed()){
            Socket socket = server.accept();
            Thread clientThread = new Thread(new ClientHandler(socket));
            clientThread.start();
            System.out.println("Client connected");
        }
    }
}

class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

        @Override
        public void run(){
            try(OutputStreamWriter oos = new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8);
                BufferedWriter wr = new BufferedWriter(oos);
                InputStreamReader ios = new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(ios);){



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
                        InputStream is = new FileInputStream("ls.txt");
                        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                        BufferedReader cat = new BufferedReader(reader);
                        String line;
                        wr.write("FILES :\n");
                        while ((line = cat.readLine()) != null) {
                            wr.write(line + "\n");
                            wr.flush();
                        }
                        wr.write("|||\n");
                        wr.flush();
                    } else if(message.startsWith("CAT")) {
                        if(message.length() <= 4) {
                            wr.write("ERROR 3\n");
                            wr.flush();
                            wr.write("|||\n");
                            wr.flush();
                        } else {
                            boolean fileExist = false;
                            String filename = message.substring(4);
                            InputStream isLs = new FileInputStream("ls.txt");
                            Reader rd = new InputStreamReader(isLs, StandardCharsets.UTF_8);
                            BufferedReader ls = new BufferedReader(rd);
                            String line;
                            while ((line = ls.readLine()) != null) {
                                if(line.equals(filename)){
                                    fileExist = true;
                                }
                            }
                            ls.close();
                            rd.close();
                            isLs.close();
                            String[] token = message.split(" ");
                            if(token.length != 2 || !fileExist){
                                wr.write("ERROR 3\n");
                                wr.flush();
                            }else{
                                InputStream is = new FileInputStream(filename);
                                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                                BufferedReader cat = new BufferedReader(reader);
                                while ((line = cat.readLine()) != null) {
                                    wr.write(line + "\n");
                                    wr.flush();
                                }
                                wr.write("|||\n");
                                wr.flush();
                                cat.close();
                                reader.close();
                                is.close();
                            }
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
                            int result;
                            if(token[0].startsWith("ADD")) {
                                if(token.length != 3){
                                    result = 2;
                                }else{
                                    result = Add.addVariableToFile(filename, token[1],token[2]);
                                }
                                if(result == 0){
                                    wr.write("VARIABLE CREATED\n");
                                }else{
                                    wr.write("ERROR : " + result + "\n");
                                }
                                wr.flush();
                            }else if(token[0].startsWith("DELETE")) {
                                if(token.length != 2){
                                    result = 2;
                                }else{
                                    result = Delete.deleteVariableToFile(filename, token[1]);
                                }
                                if(result == 0){
                                    wr.write("VARIABLE DELETED\n");
                                }else{
                                    wr.write("ERROR : "+result+"\n");
                                }
                                wr.flush();
                            }else if(token[0].startsWith("MODIFY")) {
                                if(token.length != 3){
                                    result = 2;
                                }else{
                                    result = Modify.modifyVar(filename, token[1], token[2]);
                                }
                                if(result == 0){
                                    wr.write("VARIABLE VALUE MODIFIED\n");
                                }else{
                                    wr.write("ERROR : "+result+"\n");
                                }
                                wr.flush();
                            }else if(token[0].startsWith("RENAME")) {
                                if(token.length != 3){
                                    result = 3;
                                }else{
                                    result = Rename.renameVar(filename, token[1], token[2]);
                                }
                                if(result == 0){
                                    wr.write("VARIABLE NAME MODIFIED\n");
                                }else{
                                    wr.write("ERROR : "+result+"\n");
                                }
                                wr.flush();
                            }else{
                                wr.write("ERROR\n");
                                wr.flush();
                            }
                        }
                    } else {
                        wr.write("ERROR : PLEASE SELECT A OPTION \n");
                        wr.flush();
                    }
                }
                oos.close();
                socket.close();
                System.out.println("Closing connexion");
            }catch(IOException e){
                System.out.println("Error : "+e);
            }
        }
    }


