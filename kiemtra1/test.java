/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kiemtra1;

/**
 *
 * @author sonchubeo
 */
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class test extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private PrintWriter writer;
    private String name;

    private JTextArea chatArea;
    private JTextField messageField;
    private JLabel imageLabel; // Thêm JLabel để hiển thị ảnh

    public test() {
        // Hộp thoại để người dùng nhập tên
        name = JOptionPane.showInputDialog("Enter your name:");

        setTitle("Chat test");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(new SendMessageListener());
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendMessageListener());
        bottomPanel.add(sendButton, BorderLayout.EAST);

        JButton attachButton = new JButton("Attach Image");
        attachButton.addActionListener(new AttachImageListener());
        bottomPanel.add(attachButton, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Thêm JLabel để hiển thị ảnh
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.NORTH);

        setVisible(true);

        connectToServer();
        startReading();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(name); // Gửi tên của người dùng tới server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReading() {
        Thread readThread = new Thread(() -> {
            try {
                Scanner serverScanner = new Scanner(socket.getInputStream());
                while (serverScanner.hasNextLine()) {
                    String message = serverScanner.nextLine();
                    if (message.startsWith("IMAGE:")) {
                        displayImage(message.substring(6)); // Hiển thị ảnh
                    } else {
                        chatArea.append(message + "\n"); // Hiển thị tin nhắn từ server
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    private void displayImage(String imageName) {
        try {
            BufferedImage image = ImageIO.read(new File(imageName)); // Đọc ảnh từ file
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon); // Hiển thị ảnh trên JLabel
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messageField.getText();
            writer.println(name + ": " + message); // Gửi tin nhắn với tên của người gửi
            messageField.setText("");
        }
    }

    private class AttachImageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(test.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sendImage(selectedFile);
            }
        }

        private void sendImage(File file) {
            try {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(imageData);
                outputStream.flush();

                chatArea.append("Image sent: " + file.getName() + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(test::new);
    }
}


