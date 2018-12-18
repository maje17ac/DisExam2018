package com.cbsexam;

import cache.ProductCache;
import com.google.gson.Gson;
import controllers.ProductController;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Product;
import utils.Encryption;

//MAIKEN NOTES: Alle de endpoints som er fra productklassen, bruker path "product"
@Path("product")
public class ProductEndpoints {

    //MAIKEN NOTES: Oppretter et objekt av OrderCache klassen, skal være statisk da vi bruger de i statiske metoder i controllerne
    public static ProductCache productCache;

    public ProductEndpoints() {
        this.productCache = new ProductCache();
    }

    /**
     * @param idProduct
     * @return Responses
     */
    @GET
    @Path("/{idProduct}")
    public Response getProduct(@PathParam("idProduct") int idProduct) {

        // Call our controller-layer in order to get the order from the DB
        Product product = ProductController.getProduct(idProduct);

        // TODO: Add Encryption to JSON: FIXED
        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(product);

        //MAIKE NTOES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);

        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (idProduct != 0) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get product").build();
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
    public Response getProducts() {

        // MAIKEN NOTES: Kaller på metoden getProducts i Cache klassen, slik at vi kan hente produktene gjennom den og force update true/false (som igjen henter produktene fra controllers)
        ArrayList<Product> products = productCache.getProducts(false);

        // TODO: Add Encryption to JSON: FIXED
        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(products);

        //MAIKEN NOTES: Krypterer json String, ved å kalle på algoritmen som ligger i klassen Encryption som nå tar json String som parameter for rawstring
        json = Encryption.encryptDecryptXOR(json);


        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (products != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not get products").build();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            }
        return null;
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(String body) {

        // Read the json from body and transfer it to a product class
        Product newProduct = new Gson().fromJson(body, Product.class);

        // Use the controller to add the user
        Product createdProduct = ProductController.createProduct(newProduct);

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createdProduct);



        //MAIKEN NOTES: Legger til response status, og if/else slik at det returnerer to ulike responser avhengig av betingelsene i statementet
        try {
            if (createdProduct != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not create product").build();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
    }

}

