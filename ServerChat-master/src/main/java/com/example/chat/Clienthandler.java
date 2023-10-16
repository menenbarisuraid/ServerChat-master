package com.example.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

class ClientHandler {
    Socket socket;
    ServerMain server;
    DataOutputStream out;
    DataInputStream in;
    private String nickname;
    private String name;
    private String password;
    public String getNickname(){
        return nickname;
    }
    private List<String> myblocks;
    public List<String> getMyblocks(){
        return myblocks;
    }public String getName(){
        return name;
    }public String getPassword(){
        return password;
    }

    public ClientHandler(Socket socket,ServerMain serverMain) {
        this.socket = socket;
        this.server = serverMain;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            myblocks = new LinkedList<>();

            new Thread(() -> {
                try {
                    while (true){
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String[] creds = str.split(" ");
                            name = creds[1];
                            password = creds[2];
                            nickname = AuthServer.getNickByLoginPass(creds[1],creds[2]);

                            if (isUserCorrect(nickname,server)){
                                break;}
                        }


                    }

                    while (true) {
                        String str;
                        str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/end");
                            break;}
                        else if(str.startsWith("/close")){
                            server.unsubscribe(ClientHandler.this);
                        }else if(str.startsWith("/changenick")){
                            String strs[] = str.split(" ");
                            AuthServer.changenick(strs[1],this);
                        }
                        else if(str.startsWith("/self")){
                            System.out.println("inself");
                            String[] strs = str.split(" ");
                            sendMsg(nickname + ":" + str.substring(str.indexOf(strs[1])+strs[1].length()));
                            serverMain.sendtoself(strs[1],nickname,str.substring(str.indexOf(strs[1])+strs[1].length()),this);
                        }
                        else if(str.startsWith("/block")){
                            System.out.println("inblock");
                            String[] strs = str.split(" ");
                            block(strs[1]);
                        }else if(str.startsWith("/unblock")){
                            System.out.println("inunblock");
                            String[] strs = str.split(" ");
                            unblock(strs[1]);
                        }
                        else if(str.startsWith("/show")){
                            server.sendOnlineUsers();}
                        else{
                            serverMain.sendtoall(nickname + ":" + str,this);
                        }}
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }server.subscribe(ClientHandler.this);

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }public boolean isUserCorrect(String nick, ServerMain server) throws IOException {
        boolean isNickFree = server.isNickFree(nick);
        if(isNickFree){
            server.subscribe(ClientHandler.this);
            sendServiceMsg("/authok "+"your nick is "+nickname);
            server.sendOnlineUsers();
            return true;
        }else{
            sendMsg("Wrong Login or Password");
            return false;
        }
    }

    public void sendMsg(String msg) throws IOException {
        System.out.println("Client send a message: " + msg);
        out.writeUTF(msg + "\n");
    }public void sendServiceMsg(String msg) throws IOException {
        System.out.println("Client send a message: " + msg);
        out.writeUTF(msg + "\n");
    }public void block(String blocknick){
        myblocks.add(blocknick);
    }public void unblock(String blocknick){
        myblocks.remove(blocknick);
    }
}