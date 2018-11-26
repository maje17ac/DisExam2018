package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cbsexam.OrderEndpoints;
import com.sun.deploy.util.OrderedHashSet;
import model.Address;
import model.LineItem;
import model.Order;
import model.User;
import utils.Log;

public class OrderController {

    private static DatabaseController dbCon;

    public OrderController() {
        dbCon = new DatabaseController();
    }

    public static Order getOrder(int id) {

        // check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Build SQL string to query
        //String sql = "SELECT * FROM orders where id=" + id;
        String sql = "SELECT*,\n" +
                "billing.street_address as billing, shipping.street_address as shipping \n" +
                "FROM orders \n" +
                "JOIN USER ON user.id = orders.user_id\n" +
                "JOIN address as billing ON orders.billing_address_id = billing.id \n" +
                "JOIN address as shipping ON orders.shipping_address_id = shipping.id \n" +
                "WHERE orders.id= " + id;

        // Do the query in the database and create an empty object for the results
        ResultSet rs = dbCon.query(sql);
        Order order = null;

        try {
            if (rs.next()) {


                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));


                //MAIKEN NOTES:  OPTIMIZE!!!!!!!!!!!!
                // TODO: Perhaps we could optimize things a bit here and get rid of nested queries: FIXED
                //User user = UserController.getUser(rs.getInt("user_id"));
                ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
               // Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
                //Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

                Address billingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                Address shippingAddress =
                        new Address(
                                rs.getInt("shipping_address_id"),
                                rs.getString("name"),
                                rs.getString("shipping"),
                                rs.getString("city"),
                                rs.getString("zipcode"));


                // Create an object instance of order from the database dataa
                order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));

                // Returns the build order
                return order;
            } else {
                System.out.println("No order found");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Returns null
        return order;
    }

    /**
     * Get all orders in database
     *
     * @return
     */
    public static ArrayList<Order> getOrders() {

        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        //FIKS DETTE; EN LANG SQL LEFT JOIN, I stedet for nested queries
        //String sql = "SELECT * FROM orders";

        String sql = "SELECT*,\n" +
                "billing.street_address as billing, shipping.street_address as shipping \n" +
                "FROM orders \n" +
                "JOIN user ON user.id = orders.user_id \n" +
                "JOIN address AS billing ON orders.billing_address_id = billing.id\n" +
                "JOIN address AS shipping ON orders.shipping_address_id = shipping.id\n";

        ResultSet rs = dbCon.query(sql);
        ArrayList<Order> orders = new ArrayList<Order>();

        try {
            while (rs.next()) {

                //MAIKEN NOTES:  OPTIMIZE!!!!!!!!!!!!
                //TODO: Perhaps we could optimize things a bit here and get rid of nested queries : FIXED

                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));


                //User user = UserController.getUser(rs.getInt("user_id"));
               ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
               //Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
               //Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

                Address billingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                Address shippingAddress =
                        new Address(
                                rs.getInt("shipping_address_id"),
                                rs.getString("name"),
                                rs.getString("shipping"),
                                rs.getString("city"),
                                rs.getString("zipcode"));


                // Create an order from the database data
                Order order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));

                // Add order to our list
                orders.add(order);

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // return the orders
        return orders;
    }


    public static Order createOrder(Order order){


        // Write in log that we've reach this step
        Log.writeLog(OrderController.class.getName(), order, "Actually creating a order in DB", 0);

        // Set creation and updated time for order.
        order.setCreatedAt(System.currentTimeMillis() / 1000L);
        order.setUpdatedAt(System.currentTimeMillis() / 1000L);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Save addresses to database and save them back to initial order instance
        order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
        order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

        // Save the user to the database and save them back to initial order instance
        order.setCustomer(UserController.createUser(order.getCustomer()));

        //MAIKEN NOTES: Har tatt i bruk interfacet connection, slik at vi ikke gemmer noget hvis det feiler for noen av inputs.
        // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts : FIXED

        //Setter connection til null
        Connection connection = dbCon.getConnection();

        // Try catch med SQL exception i tilfelle feil i databasen
        try {
            //Setter autocommit på connection til å være false
            connection.setAutoCommit(false);
            // Insert the product in the DB
            int orderID = dbCon.insert(
                    "INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, created_at, updated_at) VALUES("
                            + order.getCustomer().getId()
                            + ", "
                            + order.getBillingAddress().getId()
                            + ", "
                            + order.getShippingAddress().getId()
                            + ", "
                            + order.calculateOrderTotal()
                            + ", "
                            + order.getCreatedAt()
                            + ", "
                            + order.getUpdatedAt()
                            + ")");


            //throw new SQLException();

            if (orderID != 0) {
                //Update the productid of the product before returning
                order.setId(orderID);

            }

            // Create an empty list in order to go trough items and then save them back with ID
            ArrayList<LineItem> items = new ArrayList<LineItem>();

            // Save line items to database
            for (LineItem item : order.getLineItems()) {
                item = LineItemController.createLineItem(item, order.getId());
                items.add(item);
            }

            order.setLineItems(items);

             /* Gjør om alle endringer i current transaction, and releases any database locks som evt. er holdt.
             Denne metoden brukes kun når vi har satt autcommit til false */
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                /* Realiserer connection objektet's database og JDBC ressurser øyeblikkelig,
                 i stedet for å vente på at de blir automatisk realisert */
                connection.rollback();
                System.out.println("Rollback");
            } catch (SQLException e1) {
                System.out.println("Rollback did not work" + e1.getMessage());
            } finally {
                try {
                    //Setter Autocommit til true, da vi har kalt metodene commit og rollback og denne linje heter altså current auto.commit mode.
                    connection.setAutoCommit(true);
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }

        }


        //LEGG til ORDERCACHE her
        OrderEndpoints.orderCache.getOrders(true);


        // Return order
        return order;
    }
}
