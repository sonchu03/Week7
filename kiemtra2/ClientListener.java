/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kiemtra2;

import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author sonchubeo
 */
public class ClientListener implements Runnable{
    private Socket Socket;
    private InputStream Input;
    
    public ClientListener (Socket socket){
        this.Socket=socket;
        try {
            this.Input = Socket.getInputStream();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead= Input.read(buffer))!=-1){
                String mes = new String (buffer,0,bytesRead);
                System.out.println(mes);
            }
        } catch (Exception e) {
        }
    }
}
