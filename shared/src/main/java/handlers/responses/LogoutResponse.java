package handlers.responses;

public class LogoutResponse {
    String message;

    public LogoutResponse(){}

    public LogoutResponse(String mess){
        this.message = mess;
    }

    public String getMessage() {
        return message;
    }
}
