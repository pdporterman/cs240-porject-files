package webSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

import exception.ResponseException;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    notificationHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authtoken, int gameid, ChessGame.TeamColor color) throws ResponseException {
        try {
            var action = new JoinPlayer(authtoken, gameid, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(String authtoken, int gameid) throws ResponseException {
        try {
            var action = new JoinObserver(authtoken, gameid);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authtoken, int gameId) throws ResponseException {
        try {
            var action = new Leave(authtoken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authtoken, int gameId) throws ResponseException {
        try {
            var action = new Resign(authtoken,gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authtoken, int gameId, ChessMove move) throws ResponseException {
        try{
            var action = new MakeMove(authtoken, gameId, move, false);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }catch (IOException ex){
            throw new ResponseException(500,ex.getMessage());
        }
    }

}
