package com.example.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class ServerMain {

    private List<ClientHandler> clientHandlers;

    public void start() {
        ServerSocket server;
        Socket socket;

        clientHandlers = new ArrayList<>();
        Collections.synchronizedList(clientHandlers);

        try {
            AuthServer.connect();

            server = new ServerSocket(8888);
            System.out.println("Сервер запущен");

            while (true){
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket,this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AuthServer.disconnect();
    }public void sendtoall(String msg,ClientHandler c) throws IOException {
        for(ClientHandler cl:clientHandlers) {
            if(!c.getMyblocks().contains(cl.getNickname()) && !cl.getMyblocks().contains(c.getNickname())){
                cl.sendMsg(msg);}

        }}

    public void showtoall(String msg) throws IOException {
    for(ClientHandler cl:clientHandlers) {
            cl.sendMsg(msg);

    }}
    public void sendtoself(String tonick,String fromnick,String msg,ClientHandler c) throws IOException {
        for(ClientHandler cl:clientHandlers){
            if (cl.getMyblocks().contains(fromnick)){
                c.sendMsg("tonick blocked you!");
            }else{
            if(cl.getNickname().equals(tonick) && !cl.getMyblocks().contains(fromnick)){
                cl.sendMsg(fromnick+"(self): "+msg);
                break;
            }}
        }
    }

    public void sendOnlineUsers() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> list = clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList());
        for (String s: list) {
            sb.append(s).append(" ");
        }showtoall("/show " + sb.toString().trim());
    }

    public boolean isNickFree(String nick){
        for(ClientHandler client : clientHandlers){
            if(client.getNickname().equals(nick)){
                return false;
            }
        }return true;
    }


    public void subscribe(ClientHandler ch){
        clientHandlers.add(ch);
    }

    public void unsubscribe(ClientHandler ch) throws IOException {
        for(ClientHandler cl:clientHandlers) {
            if(!cl.getNickname().equals(ch.getNickname())){
                cl.sendMsg("User " + ch.getNickname() + " is out!");
            }
        }
        clientHandlers.remove(ch);
        sendOnlineUsers();
        System.out.println("in sm end");

    }
}