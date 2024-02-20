/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kiemtra2;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author sonchubeo
 */
public class Chatclient {
    private static final String url ="localhost";
    private static final int port =5000;
    public void startClient(){
        try {
            Socket socket= new Socket(url,port);
            System.out.println("connected to server");
            ClientListener clien = new ClientListener(socket);
            new Thread(clien).start();
            OutputStream output = socket.getOutputStream();
            Scanner sc = new Scanner(System.in);
            while (true) {                
                String message = sc.nextLine();
                output.write(message.getBytes());
            }
        } catch (Exception e) {
        }
    }
}
