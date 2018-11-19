package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;

import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.User;
import utils.Encryption;
import utils.Log;

//Alle de endpoints som er fra userklassen, bruker path "user"
@Path("user")
public class UserEndpoints {

    //MAIKEN NOTES: sjekk om de skal være statiske eller ikke
    public static UserCache userCache;
    private UserController userController;

    public UserEndpoints() {
        this.userCache = new UserCache();
        this.userController = new UserController();
    }

    /**
     * @param idUser
     * @return Responses
     */
    @GET
    @Path("/{idUser}")
    public Response getUser(@PathParam("idUser") int idUser) {

        // Use the ID to get the user from the controller.
        User user = UserController.getUser(idUser);

        // TODO: Add Encryption to JSON : FIXED
        // Convert the user object to json in order to return the object
        String json = new Gson().toJson(user);

        // Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);



    /* Man kunne forestille seg at det skal returneres noe annet?? Hvis man ikke kunne finne det id, så kan brugeren ha noe annet en noe tomt svar. Gjelde de andre endpoints hvis det er, showcase */
        // Return the user with the status code 200
        // TODO: What should happen if something breaks down? : (if/else and try catch on return for response status)  WORKING
        //MAIKEN NOTES:

        try {
            if (idUser != 0) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get user").build();
            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        return null;
    }

    /**
     * @return Responses
     */
    @GET
    @Path("/")
    public Response getUsers() {

        // Write to log that we are here
        Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

        //MAIKEN NOTES:
        // Get a list of users
        ArrayList<User> users = userCache.getUsers(false);

        // TODO: Add Encryption to JSON: FIXED

        // Transfer users to json in order to return it to the user
        String json = new Gson().toJson(users);

        // Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);


        //MAIKEN NOTES:

        try {
            if (users != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get user").build();
            }
        } catch (Exception e2) {
            System.out.println(e2.getMessage());
        }
        return null;
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {

        // Read the json from body and transfer it to a user class
        User newUser = new Gson().fromJson(body, User.class);

        // Use the controller to add the user
        User createUser = UserController.createUser(newUser);

        //MAIKEN NOTES:
        newUser.setPassword(newUser.getPassword());

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createUser);
        // TODO: ENCRYPTION
        // Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);

        //MAIKEN NOTES: IN CASE OF CRASH FIX TRY CATCH
        // Return the data to the user
        try {
            if (createUser != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren er opprettet" + json).build();
            } else {
                return Response.status(400).entity("Could not create user").build();
            }

        } catch (Exception e3) {
            System.out.println(e3.getMessage());
        }
        return null;
    }


    //MAIKEN NOTES:
    // TODO: Make the system able to login users and assign them a token to use throughout the system. : FIXED
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String body) {

        User user = new Gson().fromJson(body, User.class);

        String token = userController.login(user);

        // TODO: ENCRYPTION
        // Krypterer token String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        token = Encryption.encryptDecryptXOR(token);

        // Return the data to the user
        try {
            if (token != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren med id" + user.getId() + "er nå logget inn").build();
            } else {
                return Response.status(400).entity("Could not login").build();
            }
        } catch (Exception e4) {
            System.out.println(e4.getMessage());
        }
        return null;
    }


    //MAIKEN NOTES:
    // TODO: Make the system able to delete users: FIXED
    @DELETE
    @Path("/delete")
    public Response deleteUser(String token) {

        try {
            if (userController.delete(token) != false) {
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren med følgende token er nå logget inn:" + token).build();
            }
            return Response.status(400).entity("Could not login").build();
        } catch (Exception e4) {
            System.out.println(e4.getMessage());
        }
        return null;
    }


    //MAIKEN NOTES: ENDRET FRA POST TIL PUT!!!!!
    // TODO: Make the system able to update users: FIXED
    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(String body) {

// Update med bruker objektet og token i body, slik at det kan kryperes.

        User user = new Gson().fromJson(body, User.class);

        try {

            if (UserController.updateUser(user, user.getToken())) {
                userCache.getUsers(true);
                return Response.status(200).entity("Brugeren ble oppdatert").build();
            } else {
                return Response.status(400).entity("Could not update user").build();
            }
        } catch (Exception e4) {
            System.out.println(e4.getMessage());
        }
        return null;

    }

}

