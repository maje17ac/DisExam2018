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

//MAIKEN NOTES: Alle de endpoints som er fra userklassen, bruker path "user"
@Path("user")
public class UserEndpoints {

    //MAIKEN NOTES: Oppretter et objekt av OrderCache klassen, skal være statisk da vi bruger de i statiske metoder i controllerne
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

        //MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);



        // TODO: What should happen if something breaks down? : FIXED
        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (idUser != 0) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get user").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        // MAIKEN NOTES: Kaller på metoden getProducts i Cache klassen, slik at vi kan hente produktene gjennom den og force update true/false (som igjen henter produktene fra controllers)
        ArrayList<User> users = userCache.getUsers(false);

        // TODO: Add Encryption to JSON: FIXED

        // Transfer users to json in order to return it to the user
        String json = new Gson().toJson(users);

        //MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (users != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get user").build();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
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

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createUser);

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (createUser != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren er opprettet" + json).build();
            } else {
                return Response.status(400).entity("Could not create user").build();
            }

        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }


    //MAIKEN NOTES:
    // TODO: Make the system able to login users and assign them a token to use throughout the system. : FIXED
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String body) {

        //MAIKEN NOTES: Leser json fra body, og overfører det til en user klasse
        User user = new Gson().fromJson(body, User.class);

        //MAIKEN NOTES: Use the controller to add token
        String token = userController.login(user);

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (token != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren med id" + user.getId() + "er nå logget inn").build();
            } else {
                return Response.status(400).entity("Could not login").build();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }


    //MAIKEN NOTES:
    // TODO: Make the system able to delete users: FIXED
    @DELETE
    @Path("/delete")
    public Response deleteUser(String token) {

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (userController.delete(token)) {
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Brugeren med følgende token er nå slettet:" + token).build();
            } else {
                return Response.status(400).entity("Could not login").build();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return null;
    }


    //MAIKEN NOTES: Endret fra post til put, da jeg bruker post i login, og det gir mer mening og oppdatere gjennom put
    // TODO: Make the system able to update users: FIXED
    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(String body) {

        //MAIKEN NOTES: Leser json fra body, og overfører det til en user klasse (sender token og brugeren i body, slik at det kan krypteres)
        User user = new Gson().fromJson(body, User.class);

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {

            if (UserController.updateUser(user, user.getToken())) {
                userCache.getUsers(true);
                return Response.status(200).entity("Brugeren ble oppdatert").build();
            } else {
                return Response.status(400).entity("Could not update user").build();
            }
        } catch (Exception e5) {
            e5.printStackTrace();
        }
        return null;

    }

}

