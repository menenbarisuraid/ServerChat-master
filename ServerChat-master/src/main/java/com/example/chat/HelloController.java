package com.example.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class HelloController {
    TextArea ta;
    TextField tf;
    TextArea forauth;
    Button send;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    private boolean isAuthorized;
    @FXML
    private Label welcomeText;
    VBox upper = new VBox();
    BorderPane bottom = new BorderPane();
    TextArea righttextlist = new TextArea();

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    HelloController(){
        design();
        pageforauth();
    }
    public void design(){
        ta = new TextArea();
        VBox vb = new VBox();
        tf = new TextField();
        send = new Button("Send");
        send.setOnAction(e-> sendMessage("message"));
        vb.getChildren().addAll(tf,send);
        bottom.setVisible(false);
        bottom.setCenter(ta);
        bottom.setBottom(vb);
    }public void sendMessage(String s){
        try{
            String fin;
            if(s.startsWith("/")){
                fin = s;
            }else{
                fin = tf.getText();
            }
            out.writeUTF(fin);
            System.out.println("Men oslai zhazdym : " + tf.getText());
            tf.clear();
            tf.requestFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8888);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true){
                        try {
                            String str = in.readUTF();
                            if(str.startsWith("/authok")){
                                setActive(true);
                                ta.appendText(str + "\n");
                                break;
                            }else {
                                forauth.appendText(str + "\n");
                            }
                        }catch (SocketException se){
                            System.out.println("Server don't callback");
                            break;
                        }

                    }

                    while (true) {
                        try {

                            String str = in.readUTF();
                            System.out.println(str);
                            if(str.startsWith("/")){
                                if(str.equals("/end")) {
                                    break;
                                }
                                if(str.startsWith("/show")){
                                    String[] nicknames = str.split(" ");
                                    Platform.runLater(() -> {
                                        righttextlist.clear();
                                        righttextlist.appendText("ONLINE USERS");
                                        for (int i = 1; i < nicknames.length; i++) {
                                            righttextlist.appendText(nicknames[i]+"\n");
                                        }
                                    });
                                }}else{
                                Platform.runLater(()-> ta.appendText(str + "\n"));}
                        }catch (SocketException se){
                            System.out.println("Server don't callback");
                            break;

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        in.close();
                        out.close();
                        System.out.println("no connect");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }public void pageforauth(){
        TextField login = new TextField();
        login.setPromptText("Login");
        login.setMinHeight(30);
        login.setPadding(new Insets(10));
        TextField password = new TextField();
        password.setMinHeight(30);
        password.setPadding(new Insets(10));
        password.setPromptText("Password");
        Button enter = new Button();
        enter.setMinSize(100,20);
        enter.setAlignment(Pos.CENTER);
        forauth = new TextArea();
        upper.getChildren().addAll(login,password,setlikestack(enter),forauth);
        enter.setOnAction(e->{
            try {
                if(socket == null || socket.isClosed()){
                    connect();
                }if(login.getText().isBlank() || password.getText().isBlank()){
                    forauth.appendText("Input Login/Password"+"\n");
                    return;
                }
                out.writeUTF("/auth "+login.getText()+" "+password.getText());
                login.clear();
                password.clear();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
    }public void setActive(boolean isAuthorized){
        this.isAuthorized = isAuthorized;

        if(!isAuthorized){
            upper.setVisible(true);
            upper.setManaged(true);
            bottom.setVisible(false);
            bottom.setManaged(false);
            righttextlist.setVisible(false);
            righttextlist.setManaged(false);

        }else{
            upper.setVisible(false);
            upper.setManaged(false);
            bottom.setVisible(true);
            bottom.setManaged(true);
            righttextlist.setVisible(true);
            righttextlist.setManaged(true);


        }
    }public StackPane setlikestack(Node p){
        StackPane s = new StackPane();
        s.getChildren().addAll(p);
        return s;
    }


}