package com.cbsexam;

import com.google.gson.Gson;
import controllers.ReviewController;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Review;
import utils.Encryption;

//MAIKEN NOTES: Alle de endpoints som er fra reviewklassen, bruker path "search"
@Path("search")
public class ReviewEndpoints {

    /**
     * @param reviewTitle
     * @return Responses
     */
    @GET
    @Path("/title/{title}")
    public Response search(@PathParam("title") String reviewTitle) {

        // Call our controller-layer in order to get the order from the DB
        ArrayList<Review> reviews = ReviewController.searchByTitle(reviewTitle);

        // TODO: Add Encryption to JSON : FIXED
        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(reviews);

        //MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);


        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (!reviewTitle.equals(null)) {
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
}

