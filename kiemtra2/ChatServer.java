/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kiemtra2;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sonchubeo
 */
public class ChatServer {
    private static final int port=5000;
    private List<ClientHandle> cliens = new ArrayList<>();
    
    public void startServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server start "+port);
            while (true) {                
                Socket cliensocket = serverSocket.accept();
                System.out.println("new client cont"+cliensocket.getInetAddress().getHostAddress());
                ClientHandle clientHandle = new ClientHandle(cliensocket,System.currentTimeMillis()+"",this);
                cliens.add(clientHandle);
                new Thread(clientHandle).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void boardcastMes(String id,String mes) {
        for(ClientHandle client: cliens){
            if(!(client.GetId().equals(id+" : "+mes)))
            client.sendmes(id+" : "+mes);
        }
    }
}
