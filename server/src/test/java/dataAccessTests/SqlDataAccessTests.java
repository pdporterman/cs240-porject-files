package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthToken;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import server.handlers.requests.CreateGameRequest;

import java.util.Collection;

public class SqlDataAccessTests {

    DataAccess da;

    {
        try {
            da = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void cleanUp() throws TestException, DataAccessException {
        da.clearGames();
        da.clearAuth();
        da.clearUsers();
    }

    @Test
    @DisplayName("Clear Success")
    public void clearGameTest() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("game");
        da.addGame(request);
        Assertions.assertEquals(da.getAllGames().size(), 1);
        da.clearGames();
        Assertions.assertEquals(da.getAllGames().size(),0, "does not have len of 0 when cleared");
    }

    @Test
    @DisplayName("Clear User Success")
    public void clearUserTest() throws DataAccessException {
        da.addUser(new User("user", "password", "email"));
        da.clearUsers();
        User user = da.getUser("user", "password");
        Assertions.assertNull(user);
    }

    @Test
    @DisplayName("Clear Success")
    public void clearAuthTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        da.clearAuth();
        Assertions.assertFalse(da.checkAuth("token"));
    }

    @Test
    @DisplayName("add and get user success")
    public void userSuccessTest() throws DataAccessException {
        da.addUser(new User("user", "pass", "mail"));
        User user = da.getUser("user", "pass");
        Assertions.assertEquals(user.getUserName(), "user");
    }

    @Test
    @DisplayName("add and get user fail")
    public void userFailTest() throws DataAccessException {
        try {
            da.addUser(new User("user", "pass", "mail"));
            User user = da.getUser("username", "pass");
            if (user != null){
                Assertions.fail();
            }
        }
        catch (DataAccessException ex){
            Assertions.assertTrue(ex.getMessage().contains("Error"),"needs to throw error");
        }
    }

    @Test
    @DisplayName("check success")
    public void authCheckSuccessTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        Assertions.assertTrue(da.checkAuth("token"));
    }

    @Test
    @DisplayName("check fail")
    public void authCheckFailTest() throws DataAccessException {
        try {
            if (da.checkAuth("token")){
                Assertions.fail();
            }
        }
        catch (DataAccessException ex){
            Assertions.assertTrue(ex.getMessage().contains("Error"),"needs to throw error");
        }
    }

    @Test
    @DisplayName("auth get success")
    public void authGetSuccessTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        AuthToken auth = da.getAuth("token");
        Assertions.assertEquals(auth.getUsername(), "user");
    }

    @Test
    @DisplayName("auth get success")
    public void authGetFailTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        AuthToken auth = da.getAuth("auth");
        Assertions.assertNull(auth);
    }

    @Test
    @DisplayName("auth delete success")
    public void authDeleteSuccessTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        AuthToken auth = da.getAuth("token");
        Assertions.assertEquals(auth.getUsername(), "user");
        da.deleteAuth(auth.getToken());
        Assertions.assertFalse(da.checkAuth("token"));
    }

    @Test
    @DisplayName("auth delete success")
    public void authDeleteFailTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        AuthToken auth = da.getAuth("token");
        Assertions.assertEquals(auth.getUsername(), "user");
        da.deleteAuth("fail");
        Assertions.assertTrue(da.checkAuth("token"));
    }


    @Test
    @DisplayName("add check and delete auth fail")
    public void authFullFailTest() throws DataAccessException {
        try {
            da.addAuth(new AuthToken("user", "token"));
            if (da.checkAuth("auth")) {
                AuthToken auth = da.getAuth("token");
                da.deleteAuth(auth.getToken());
            }
        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("Error"), "needs to throw error");
        }
    }

    @Test
    @DisplayName("add check and delete auth fail")
    public void authFullSuccessTest() throws DataAccessException {
        da.addAuth(new AuthToken("user", "token"));
        if (da.checkAuth("token")) {
            AuthToken auth = da.getAuth("token");
            da.deleteAuth(auth.getToken());
        }
    }

    @Test
    @DisplayName("add and get for game success")
    public void gameGetSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Game temp = da.getGame(game.getGameID());
        Assertions.assertEquals(temp.getGameID(), 1);
    }

    @Test
    @DisplayName("add and get for game fail")
    public void gameGetFailTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Game temp = da.getGame(5);
        Assertions.assertNull(temp);
    }

    @Test
    @DisplayName("get game list")
    public void gameListSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Collection<Game> list = da.getAllGames();
        Assertions.assertEquals(list.size(), 1);
    }
    

    @Test
    @DisplayName("get multiple game list")
    public void gameListMultipleTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Game game2 = da.addGame(new CreateGameRequest("game2"));
        Collection<Game> list = da.getAllGames();
        Assertions.assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("add and set black player for game success")
    public void gameSetBlackPlayerSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        da.setPlayer("user", "BLACK",game);
        Game temp = da.getGame(game.getGameID());
        Assertions.assertEquals(temp.getBlackUsername(), "user");
    }

    @Test
    @DisplayName("add and set white player for game success")
    public void gameSetWhitePlayerSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        da.setPlayer("user", "WHITE",game);
        Game temp = da.getGame(game.getGameID());
        Assertions.assertEquals(temp.getWhiteUsername(), "user");
    }

    @Test
    @DisplayName("add and set watcher for game success")
    public void gameSetWatchSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        da.setPlayer("user", "",game);
        Game temp = da.getGame(game.getGameID());
        Assertions.assertNull(temp.getWhiteUsername());
        Assertions.assertNull(temp.getBlackUsername());
    }

    @Test
    @DisplayName("add and set watcher for game fail")
    public void gameSetFailTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Game temp = new Game(52, "name");
        Assertions.assertFalse(da.setPlayer("user", "",temp));
    }

    @Test
    @DisplayName("add list get and set player for game success")
    public void gameFullSuccessTest() throws DataAccessException {
        Game game = da.addGame(new CreateGameRequest("game"));
        Collection<Game> list = da.getAllGames();
        Assertions.assertEquals(list.size(), 1);
        da.setPlayer("user", "BLACK",game);
        Game temp = da.getGame(game.getGameID());
        Assertions.assertEquals(temp.getBlackUsername(), "user");
    }

    @Test
    @DisplayName("add list get and set player for game fail")
    public void gameFullFailTest() throws DataAccessException {
        try {
            Game game = da.addGame(new CreateGameRequest("game"));
            Collection<Game> list = da.getAllGames();
            Assertions.assertEquals(list.size(), 1);
            da.setPlayer("user", "BLACK", game);
            Game temp = da.getGame(5);

        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("Error"), "needs to throw error");
        }
    }




}
