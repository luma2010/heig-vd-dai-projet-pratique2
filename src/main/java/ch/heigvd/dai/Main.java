package ch.heigvd.dai;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import picocli.CommandLine;

@CommandLine.Command(
        description = "VarMod connector is a variable modifier for server-client",
        version = "1.0.0",
        scope = CommandLine.ScopeType.INHERIT,
        mixinStandardHelpOptions = true
)


public class Main implements Runnable{

    @CommandLine.Parameters(
            index = "0",
            description = "The port of the server"
    )
    protected int port;

    @CommandLine.Parameters(
            index = "1",
            description = "server or client"
    )
    protected String type;


    @CommandLine.Parameters(
            index = "2",
            description = "IP of the server/client"
    )
    protected String addr;

    public void run() {
        if (type.equals("server")) {
            ServerSocket server = null;
            InetAddress address = null;
            SocketAddress servAddr = null;
            try {
                address = InetAddress.getByName(addr);
                servAddr = new InetSocketAddress(address,port);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            try {
                server = new ServerSocket();
                server.bind(servAddr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Waiting for client...");
            while (!server.isClosed()) {
                Socket socket = null;
                try {
                    socket = server.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start();
                System.out.println("Client connected");
            }
        } else if (type.equals("client")) {
            InetAddress host = null;
            try {
                host = InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            Socket socket = null;
            OutputStreamWriter oos = null;
            InputStreamReader ois = null;
            Scanner myInput = new Scanner(System.in);
            try {
                socket = new Socket(host.getHostName(), port);
                oos = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                ois = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BufferedWriter wr = new BufferedWriter(oos);
            BufferedReader br = new BufferedReader(ois);
            String message = null;
            try {
                message = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(message);

            while (true) {
                for (int i = 0; i < 5; i++) {
                    try {
                        message = br.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(message);
                }
                String input = myInput.nextLine() + "\n";
                try {
                    wr.write(input);
                    wr.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (input.startsWith("EXIT")) {
                    break;
                } else if (input.startsWith("VARIABLES") && input.length() > 10) {
                    for (int i = 0; i < 5; i++) {
                        try {
                            message = br.readLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(message);
                    }
                    input = myInput.nextLine() + "\n";
                    try {
                        wr.write(input);
                        wr.flush();
                        message = br.readLine();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(message + "\n");
                } else if (input.startsWith("CAT") && input.length() > 4) {
                    while (true) {
                        try {
                            message = br.readLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (message.startsWith("|||")) {
                            break;
                        }
                        System.out.println(message);
                    }
                    System.out.println();
                } else if (input.startsWith("LS")) {
                    while (true) {
                        try {
                            message = br.readLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (message.startsWith("|||")) {
                            break;
                        }
                        System.out.println(message);
                    }
                    System.out.println();
                } else {
                    try {
                        message = br.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(message + "\n");
                }

            }
            try {
                ois.close();
                oos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public static void main(String[] args){
        int exitCode = new CommandLine(new Main()).execute(args);

        System.exit(exitCode);
    }

    /*public static void main(String[] args) throws IOException {
        int port = 4444;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Waiting for client...");
        while(!server.isClosed()){
            Socket socket = server.accept();
            Thread clientThread = new Thread(new ClientHandler(socket));
            clientThread.start();
            System.out.println("Client connected");
        }
    }*/
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
                        wr.write("ERROR 1\n");
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
                            wr.write("ERROR 2\n");
                            wr.flush();
                            wr.write("|||\n");
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
                        wr.write("ERROR 1\n");
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