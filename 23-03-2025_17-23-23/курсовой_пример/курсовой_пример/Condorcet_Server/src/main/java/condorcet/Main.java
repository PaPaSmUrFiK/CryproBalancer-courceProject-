package condorcet;

import condorcet.Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int PORT_NUMBER = 5555;
    private static ServerSocket serverSocket;
    private static ClientThread clientHandler;
    private static Thread thread;
    private static List<Socket> currentSockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        while (true) {
            for (Socket socket :
                    currentSockets) {
                if(socket.isClosed()){
                    currentSockets.remove(socket);
                    continue;
                }
                String socketInfo = "Клиент " + socket.getInetAddress() + ":" + socket.getPort() + " подключен.";
                System.out.println(socketInfo);
            }
            Socket socket = serverSocket.accept();
            currentSockets.add(socket);
            clientHandler = new ClientThread(socket);
            thread = new Thread(clientHandler);
            thread.start();
            System.out.flush();
        }
    }

    protected void finalize() throws IOException {
        serverSocket.close();
    }
}
