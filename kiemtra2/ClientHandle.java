/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kiemtra2;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author sonchubeo
 */
public class ClientHandle implements Runnable{
    private Socket mysocket;
    private ChatServer chatServer;
    private String id;
    private InputStream input;
    private OutputStream output;
    public ClientHandle(Socket mySocket, String id,ChatServer chatServer ){
        this.mysocket=mySocket;
        this.id=id;
        this.chatServer =chatServer;
        try {
            this.input = mySocket.getInputStream();
            this.output = mySocket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead= input.read(buffer))!=-1){
                String mes = new String (buffer,0,bytesRead);
                chatServer.boardcastMes(this.id,mes);
            }
        } catch (Exception e) {
        }
    }
    public void sendmes(String mes){
        try {
            output.write(mes.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String GetId(){
        return id;
    }
   
    
}
