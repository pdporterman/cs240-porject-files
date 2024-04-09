package ui;

import static ui.EscapeSequences.*;

import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Scanner;

import chess.ChessGame;
import model.AuthToken;
import model.Game;
import server.handlers.requests.*;
import server.handlers.responses.*;
import serverFacade.ResponseException;
import serverFacade.ServerFacade;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import webSocketMessages.serverMessages.NotificationMessages;
import webSocketMessages.userCommands.JoinPlayerCommand;

public class ConsoleUI implements NotificationHandler {
    private String token = null;

    private String playerColor = null;
    private ChessGame game = null;
    private final PrintChess printer = new PrintChess();
    private final ServerFacade server = new ServerFacade();
    private final Scanner scanner = new Scanner(System.in);

    private final WebSocketFacade websocket = new WebSocketFacade("http://localhost:3000", this);

    public ConsoleUI() throws exception.ResponseException {
    }

    public String login(){
        try {
            System.out.print("please enter username: ");
            String user = scanner.next();
            System.out.print("please enter password: ");
            String pass = scanner.next();
            LoginResponse response = server.login(new LoginRequest(user, pass));
            token = response.getAuthToken();

            return "logged in";
        } catch (Exception e) {
            return "failed to log in";
        }
    }
    public String register(){
        try {
            System.out.print("please enter username: ");
            String user = scanner.next();
            System.out.print("please enter password: ");
            String pass = scanner.next();
            System.out.print("please enter email: ");
            String email = scanner.next();
            RegisterResponse response = server.register(new RegisterRequest(user, pass, email));
            token = response.getAuthToken();
            return "user created and logged in";
        } catch (Exception e) {
            return "failed to create user (" + e.toString() + ")";
        }
    }
    public String logout(){
        try {
            LogoutResponse response = server.logout(new LogoutRequest(token));
            token = null;
            return "logged out";
        } catch (ResponseException e) {
            return "failed to log out (" + e.toString() + ")";
        }
    }
    public String createGame(){
        try {
            System.out.print("please enter a game name: ");
            String name = scanner.next();
            CreateGameRequest request = new CreateGameRequest(name);
            request.setAuth(token);
            CreateGameResponse response = server.createGame(request);
            return "game created";
        } catch (Exception e) {
            return "failed to create game (" + e.toString() + ")";
        }
    }
    private String observeGame() {
        try {
            System.out.print("please enter a gameID number: ");
            String gameid = scanner.next();
            JoinGameRequest request = new JoinGameRequest(Integer.parseInt(gameid));
            request.playerColor = null;
            request.setAuthorization(token);
            JoinGameResponse response = server.joinGame(request);
//            printer.displayBoard();
            return SET_BG_COLOR_BLACK + "observing game";
        } catch (Exception e) {
            return "failed to join game (" + e.toString() + ")";
        }
    }
    public String joinGame(){
        try {
            System.out.print("please enter a gameID number: ");
            String gameid = scanner.next();
            System.out.print("please enter color (WHITE / BLACK): ");
            String color = scanner.next();
            JoinGameRequest request = new JoinGameRequest(Integer.parseInt(gameid));
            request.playerColor = color;
            playerColor = color;
            request.setAuthorization(token);
            JoinGameResponse response = server.joinGame(request);
            websocket.joinGame(new JoinPlayerCommand(token, gameid, playerColor));
//            printer.displayBoard();
            return SET_BG_COLOR_BLACK + "joined game as " + color;
        } catch (Exception e) {
            return "failed to join game (" + e.toString() + ")";
        }
    }
    public String listGame(){
        try {
            ListGamesResponse response = server.listGame(new ListGamesRequest(token));
            return "these are the games\n" + response.gamesToString();
        } catch (Exception e) {
            return "failed to get games (" + e.getMessage() + ")";
        }
    }
    public String clear(){
        try {
            System.out.print("please enter password to clear: ");
            String pass = scanner.next();
            if (Objects.equals(pass, "doTheThing")){
                server.clear();
                return "cleared DB";
            }
            return "incorrect Password";
        } catch (Exception e) {
            return "failed clear DB (" + e.toString() + ")";
        }
    }


    public String eval(String input){
        String word = input.toLowerCase();
        return switch (word){
            case "login" -> login();
            case "register" -> register();
            case "logout" -> logout();
            case "create game" -> createGame();
            case "join game" -> joinGame();
            case "observe game" -> observeGame();
            case "list games" -> listGame();
            case "clear" -> clear();
            case "quit" -> "good bye";
            case "help" -> "just type out the action you wish to take, for example to log in type 'login'";
            default -> "invalid input, please enter response matching available options";
        };
    }



    public String menu() {
        if (Objects.equals(token, null)) {
            return SET_BG_COLOR_DARK_GREY + """
                    - login
                    - register
                    - help
                    - quit
                    """;
        }
        else if (game != null){
            return SET_BG_COLOR_DARK_GREY + """
                - make move
                - highlight moves
                - redraw board
                - resign
                - leave
                - help
                """;
        }
        else{
            return SET_BG_COLOR_DARK_GREY + """
                - logout
                - create game
                - join game
                - observe game
                - list games
                - help
                - quit
                """;
        }

    }

    @Override
    public void notify(NotificationMessages notification) {
        System.out.println(notification.getMessage());
    }
}
