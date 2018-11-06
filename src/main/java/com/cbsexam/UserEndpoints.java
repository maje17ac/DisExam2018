package com.cbsexam;
import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

//Alle de endpoints som er fra userklassen, bruker path "user"
@Path("user")
public class UserEndpoints {

    //MAIKEN NOTES:
    private static UserCache userCache;

    public UserEndpoints(){
        this.userCache = new UserCache();
    }

    /**
     * @param idUser
     * @return Responses
     */
    // her bruker man bath user/iduser
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
        // TODO: What should happen if something breaks down?
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
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


        // Return the users with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
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

        // Return the data to the user
        if (createUser != null) {
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        } else {
            return Response.status(400).entity("Could not create user").build();
        }
    }

    // TRE Endpoints som ikke er laget enda, implementer logikken, endpointet er der,
    //MAIKEN NOTES:
    // TODO: Make the system able to login users and assign them a token to use throughout the system. :
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String body) {





            return Response.status(400).entity("Could not login").build();

    }


/*

    // TODO: Make the system able to delete users: WORKING
    @POST
    @Path("/delete/{idUSER}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(String body) {
        int id = 0;

        UserController.delete(id);

        userCache.getUsers(true);

        return Response.status(400).entity("Deleted the user with id" + id).build();
    }




    // TODO: Make the system able to update users:
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(String body) {
        // Read the json from body and transfer it to a user class
        User newUser = new Gson().fromJson(body, User.class);

        // Use the controller to add the user
        User updateUser = UserController.updateUser(newUser);

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(updateUser);

        // Return the data to the user
        if (updateUser != null) {
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        } else {
            return Response.status(400).entity("Could not login").build();
        }
    }

        //MAIKEN NOTES:
        // Get a list of users
        ArrayList<User> users = userCache.getUsers(true);  ????
    */
}
