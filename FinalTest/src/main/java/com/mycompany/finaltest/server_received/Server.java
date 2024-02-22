package com.mycompany.finaltest.server_received;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static final int MAX_CONNECTIONS = 10; // Số kết nối tối đa cho phép

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                if (clients.size() < MAX_CONNECTIONS) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);

                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                } else {
                    System.out.println("Max connections reached. Cannot accept new clients.");
                    // Có thể thực hiện các hành động khác tại đây
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String name = reader.readLine(); // Đọc tên từ client
                System.out.println(name + " has joined the chat.");
                broadcastMessage(name + " has joined the chat.");

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    if (message.startsWith("[FILE]")) {
                        String fileName = message.substring(6);
                        receiveAndBroadcastFile(fileName);
                    } else {
                        broadcastMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(this); // Loại bỏ client đã đóng kết nối khỏi danh sách
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                client.writer.println(message);
            }
        }

        private void receiveAndBroadcastFile(String fileName) throws IOException {
    // Broadcast file name to all clients
    for (ClientHandler client : clients) {
        if (client != this) {
            client.writer.println("[FILE]" + fileName);
        }
    }
}
    }
}
