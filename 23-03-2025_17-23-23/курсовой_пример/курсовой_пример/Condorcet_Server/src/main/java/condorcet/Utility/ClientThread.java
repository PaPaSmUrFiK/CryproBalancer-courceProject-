package condorcet.Utility;

import com.google.gson.Gson;
import condorcet.Enums.ResponseStatus;
import condorcet.Models.Entities.*;
import condorcet.Models.ResultMark;
import condorcet.Models.TCP.Request;
import condorcet.Models.TCP.Response;
import condorcet.Services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;


    private UserService userService = new UserService();
    private FlightService flightService = new FlightService();
    private PassengerService passengerService = new PassengerService();
    private PersonDataService personDataService = new PersonDataService();
    private RouteService routeService = new RouteService();
    private UserMarkService userMarkService = new UserMarkService();
    private AircraftService aircraftService = new AircraftService();

    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();

                request = gson.fromJson(message, Request.class);
                switch (request.getRequestType()) {
                    case REGISTER: {
                        User user = gson.fromJson(request.getRequestMessage(), User.class);
                        if (userService.findAllEntities().stream().noneMatch(x -> x.getLogin().toLowerCase().equals(user.getLogin().toLowerCase()))) {
                            personDataService.saveEntity(user.getPersonData());
                            userService.saveEntity(user);
                            userService.findAllEntities();
                            User returnUser;
                            returnUser = userService.findEntity(user.getId());
                            returnUser.setUserMarks(null);
                            response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(returnUser));
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Такой пользователь уже существует!", "");
                        }
                        break;
                    }
                    case LOGIN: {
                        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);
                        if (userService.findAllEntities().stream().anyMatch(x -> x.getLogin().toLowerCase().equals(requestUser.getLogin().toLowerCase())) && userService.findAllEntities().stream().anyMatch(x -> x.getPassword().equals(requestUser.getPassword()))) {
                            User user = userService.findAllEntities().stream().filter(x -> x.getLogin().toLowerCase().equals(requestUser.getLogin().toLowerCase())).findFirst().get();
                            user = userService.findEntity(user.getId());
                            response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(user));
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Такого пользователя не существует или неправильный пароль!", "");
                        }
                        break;
                    }
                    case ADD_FLIGHT:
                        Flight flight = gson.fromJson(request.getRequestMessage(), Flight.class);
                        routeService.saveEntity(flight.getRoute());
                        aircraftService.saveEntity(flight.getAircraft());
                        flightService.saveEntity(flight);
                        response = new Response(ResponseStatus.OK, "Готово!", "");
                        break;
                    case DELETE_FLIGHT:
                        flight = gson.fromJson(request.getRequestMessage(), Flight.class);
                        flightService.deleteEntity(flight);
                        response = new Response(ResponseStatus.OK, "Готово!", "");
                        break;
                    case GET_FLIGHT:
                        flight = gson.fromJson(request.getRequestMessage(), Flight.class);
                        flight = flightService.findEntity(flight.getId());
                        response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(flight));
                        break;
                    case GETALL_FLIGHT:
                        List<Flight> flights = new ArrayList<>();
                        List<ResultMark> result = calcCondorcet();
                        for (ResultMark resultMark :
                                result) {
                            flights.add(resultMark.getFlight());
                        }
                        response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(flights));
                        break;
                    case UPDATE_FLIGHT:
                        flight = gson.fromJson(request.getRequestMessage(), Flight.class);
                        routeService.updateEntity(flight.getRoute());
                        aircraftService.updateEntity(flight.getAircraft());
                        flightService.updateEntity(flight);
                        response = new Response(ResponseStatus.OK, "Готово!", "");
                        break;
                    case UPDATE_MARK:
                        UserMark mark = gson.fromJson(request.getRequestMessage(), UserMark.class);
                        userMarkService.updateEntity(mark);
                        response = new Response(ResponseStatus.OK, "Готово!", "");
                        break;
                    case UPDATE_PASSENGER:
                        Passenger passenger = gson.fromJson(request.getRequestMessage(), Passenger.class);
                        List<Passenger> passengers = passengerService.findAllEntities();
                        if (passengers.stream().anyMatch(x -> x.getPlaceNumber() == passenger.getPlaceNumber() && x.getId() != passenger.getId() && x.getFlight().getId()== passenger.getFlight().getId())) {
                            response = new Response(ResponseStatus.ERROR, "Это место уже занято!", "");
                            break;
                        }
                        passengerService.updateEntity(passenger);
                        response = new Response(ResponseStatus.OK, "Готово!", "");
                        break;
                    case CONDORCET:
                        result = calcCondorcet();
                        response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(result));
                        break;
                }
                out.println(gson.toJson(response));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Клиент " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " закрыл соединение.");
            try {

                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ResultMark> calcCondorcet() {
        List<ResultMark> result = new ArrayList<>();
        List<Flight> flights = (List<Flight>) flightService.findAllEntities();
        for (Flight flight :
                flights) {
            result.add(new ResultMark(0, flight));
        }
        List<User> users = (List<User>) userService.findAllEntities();
        for (User user :
                users) {
            Set<UserMark> tempUserMarks = (Set<UserMark>) user.getUserMarks();
            if (tempUserMarks.size() != 0) {
                List<UserMark> userMarks = new ArrayList<>();
                Map<UserMark, Integer> userMarksValue = new HashMap<>();
                for (UserMark usermark :
                        tempUserMarks) {
                    userMarks.add(usermark);
                }
                Collections.sort(userMarks);
                for (UserMark userMark :
                        userMarks) {
                    userMarksValue.put(userMark, 0);
                }
                int equalMarks = 0;
                for (int i = 0; i < userMarks.size() - 1; i++) {
                    if (userMarks.get(i).getMark() == userMarks.get(i + 1).getMark())
                        equalMarks++;
                }
                int i = userMarks.size() - equalMarks;
                for (int j = 0; j < userMarks.size() - 1; j++) {
                    if (userMarks.get(j).getMark() == userMarks.get(j + 1).getMark())
                        userMarksValue.replace(userMarks.get(j), i - 1);
                    userMarksValue.replace(userMarks.get(j), i);
                    i--;
                }
                for (ResultMark resultMark :
                        result) {
                    Optional<UserMark> foundUserMark = userMarks.stream().filter(x -> x.getFlight().getId() == resultMark.getFlight().getId()).findFirst();
                    if (foundUserMark.isPresent()) {
                        resultMark.setPositionValue(resultMark.getPositionValue() + userMarksValue.get(foundUserMark.get()));
                        resultMark.setAverageMark(resultMark.getAverageMark() + foundUserMark.get().getMark());
                    }
                }
            }

        }
        for (ResultMark resultMark :
                result) {
            resultMark.setAverageMark(resultMark.getAverageMark() / users.size());
        }
        Collections.sort(result);

        return result;
    }
}
