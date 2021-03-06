package com.cbsexam;

import cache.OrderCache;
import com.google.gson.Gson;
import controllers.OrderController;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Order;
import utils.Encryption;

//MAIKEN NOTES: Alle de endpoints som er fra orderklassen, bruker path "order"
@Path("order")
public class OrderEndpoints {

    //MAIKEN NOTES: Oppretter et objekt av OrderCache klassen, skal være statisk da vi bruger de i statiske metoder i controllerne
    public static OrderCache orderCache;

    public OrderEndpoints() {
        this.orderCache = new OrderCache();
    }

    /**
     * @param idOrder
     * @return Responses
     */
    @GET
    @Path("/{idOrder}")
    public Response getOrder(@PathParam("idOrder") int idOrder) {
        try {
        // Call our controller-layer in order to get the order from the DB
        Order order = OrderController.getOrder(idOrder);

        // TODO: Add Encryption to JSON: FIXED
        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(order);

        // MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);


        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet

            if (idOrder != 0) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get order").build();
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
    public Response getOrders() {

        // MAIKEN NOTES: Kaller på metoden getOrders i Cache klassen, slik at vi kan hente ordrene gjennom den og force update true/false (som igjen henter ordrene fra controllers)
        ArrayList<Order> orders = orderCache.getOrders(false);

        // TODO: Add Encryption to JSON: FIXED
        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(orders);

        // MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);


        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet

            if (orders != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get orders").build();
            }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(String body) {

        // Read the json from body and transfer it to a order class
        Order newOrder = new Gson().fromJson(body, Order.class);

        // Use the controller to add the user
        Order createdOrder = OrderController.createOrder(newOrder);

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createdOrder);


        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (createdOrder != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not create order").build();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }
}