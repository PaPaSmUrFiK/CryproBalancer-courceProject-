package main.Utility;

import main.Models.Entities.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {
    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();

    private ClientSocket() {
        try {
            socket = new Socket("localhost", 5555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
        }
    }

    private int FlightId = -1;

    public static ClientSocket getInstance() {
        return SINGLE_INSTANCE;
    }

    private User user;
    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Socket getSocket() {
        return socket;
    }


    public BufferedReader getInStream() {
        return in;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int getFlightId() {
        return FlightId;
    }

    public void setFlightId(int flightId) {
        FlightId = flightId;
    }
    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }
}
