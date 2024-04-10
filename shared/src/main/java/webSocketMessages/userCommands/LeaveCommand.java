package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    private int gameId;
    public LeaveCommand(String authToken,int gameId) {
        super(authToken);
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

}
