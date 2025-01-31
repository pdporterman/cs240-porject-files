package handlers.responses;

import model.AuthToken;
import model.User;

public class LoginResponse {
    String username;
    String authToken;
    String message;

    public LoginResponse(User user, AuthToken auth){
        this.username = user.getUserName();
        this.authToken = auth.getToken();
    }

    public  LoginResponse(String mess){
        this.message = mess;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
