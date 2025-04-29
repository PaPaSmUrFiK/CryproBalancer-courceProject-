package main.Utility;

import com.google.gson.Gson;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable{
    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;

    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    @Override
    public void run(){

    }
}
