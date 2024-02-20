package com.mycompany.kiemtra1;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = reader.readLine(); // Đọc tên từ client

                // Gửi tên của client mới đến tất cả các client khác
                broadcastNewClientName(name);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.flush();

                // Chuyển dữ liệu ảnh đến tất cả các client
                saveImage(baos.toByteArray());

                // Gửi tin nhắn thông báo đến client rằng ảnh đã được nhận và lưu
                broadcastMessage("Image received and saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Trong phương thức broadcastMessage(), cập nhật để gửi dữ liệu ảnh đến tất cả các client
private static void broadcastMessage(String message) {
    for (PrintWriter writer : clientWriters) {
        if (message.startsWith("IMAGE:")) {
            writer.println(message); // Gửi dữ liệu ảnh đến tất cả các client
            writer.flush();
        } else {
            writer.println(message);
            writer.flush();
        }
    }
}

// Trong phương thức saveImage(), cập nhật để gửi dữ liệu ảnh với tiền tố "IMAGE:" đến các client
private static void saveImage(byte[] imageData) {
    try {
        FileOutputStream fileOutputStream = new FileOutputStream("received_image.jpg");
        fileOutputStream.write(imageData);
        fileOutputStream.close();

        // Gửi dữ liệu ảnh đến tất cả các client
        for (PrintWriter writer : clientWriters) {
            writer.println("IMAGE:received_image.jpg");
            writer.flush();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
private static void broadcastNewClientName(String name) {
        for (PrintWriter writer : clientWriters) {
            writer.println(name + " has joined the chat.");
            writer.flush();
        }
    }

}
