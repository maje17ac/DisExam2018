package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cache.UserCache;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;
import utils.Hashing;
import utils.Log;


public class UserController {
    //MAIKEN NOTES:
    private static DatabaseController dbCon;
    private static Hashing hashing;
    private String token;

    public UserController() {
        this.dbCon = new DatabaseController();
        this.hashing = new Hashing();
        this.token = token;

    }


    // sql statement som finner email og password
    //hvis den finner en bruger, inne i den når det eksekverer og ikke feiler, resultset ikke er null, så kjører
    public static User getUser(int id) {

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        UserCache userCache = new UserCache();
        userCache.getUsers(true);

        // Build the query for DB
        String sql = "SELECT * FROM user where id=" + id;

        // Actually do the query
        ResultSet rs = dbCon.query(sql);
        User user = null;

        try {
            // Get first object, since we only have one
            //MAIKEN NOTES
            if (rs.next()) {
                user =
                        new User(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getLong("created_at"));

                // return the create object
                return user;
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Return null -- MAIKE NOTES: Se på det her, returen av verdien user er alltid null, da det er definert lenger oppe (linje 47) :)
        return user;
    }

    /**
     * Get all users in database
     *
     * @return
     */
    public static ArrayList<User> getUsers() {

        // Check for DB connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Build SQL
        String sql = "SELECT * FROM user";

        // Do the query and initialyze an empty list for use if we don't get results
        ResultSet rs = dbCon.query(sql);
        ArrayList<User> users = new ArrayList<User>();

        try {
            // Loop through DB Data
            while (rs.next()) {
                User user =
                        new User(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getLong("created_at"));

                // Add element to list
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Return the list of users
        return users;
    }

    public static User createUser(User user) {
        // Write in log that we've reach this step
        Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

        // Set creation time for user.
        user.setCreatedTime(System.currentTimeMillis() / 1000L);

        //MAIKEN NOTES:
        hashing.setSalt(String.valueOf(user.getCreatedTime()));

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        // Insert the user in the DB
        // hashing algoritmer er implementert så bruk dette.
        // TODO: Hash the user password before saving it : FIX
        // MAIKEN NOTES:
        int userID = dbCon.insert(
                "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                        + user.getFirstname()
                        + "', '"
                        + user.getLastname()
                        + "', '"
                        + hashing.saltyHash(user.getPassword())  //MAIKEN NOTES: Hashing user password with salt before saving.
                        + "', '"
                        + user.getEmail()
                        + "', "
                        + user.getCreatedTime()
                        + ")");

        if (userID != 0) {
            //Update the userid of the user before returning
            user.setId(userID);

            //utils.Hashing.md5(String);???

        } else {
            // Return null if user has not been inserted into database
            return null;
        }


        // Return user
        return user;
    }


    //MAIKEN NOTES:
    public String login(User user) {

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Write in log that we've reach this step
        Log.writeLog(UserController.class.getName(), user, "Login", 0);

        // Build the query for DB
        String sql = "SELECT * FROM user WHERE email=" + user.getEmail() + " AND password=" + hashing.saltyHash(user.getPassword());

        // Actually do the query
        ResultSet rs = dbCon.query(sql);
        User loginUser = null;

        try {
            // Get first object, since we only have one
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));

                hashing.setSalt(String.valueOf(user.getCreatedTime()));
                if (user.getPassword().equals(hashing.saltyHash(user.getPassword()))) {

                    //MAIKEN NOTES: KILDE https://github.com/auth0/java-jwt
                    //FINN UT HVA HMAC256 ER FOR NOE, EVT BRUK RSA256 da dette er en mer sikker token algoritme, siden HMAC256 kan gjøre sånn at andre kan finne din kode (?)
                    try {
                        Algorithm algorithm = Algorithm.HMAC256("secret");
                        token = JWT.create()
                                .withIssuer("auth0")
                                .sign(algorithm);
                    } catch (JWTCreationException exception) {
                        //Invalid Signing configuration / Couldn't convert Claims.
                    }

                    //VERIFISERER TOKEN: SE OM JEG BURDE HA DETTE
                    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
                    try {
                        Algorithm algorithm = Algorithm.HMAC256("secret");
                        JWTVerifier verifier = JWT.require(algorithm)
                                .withIssuer("auth0")
                                .build(); //Reusable verifier instance
                        DecodedJWT jwt = verifier.verify(token);
                    } catch (JWTVerificationException exception) {
                        System.out.println(exception.getMessage());
                        //Invalid signature/claims
                    }
                    //Returnerer token direkte
                    return token;
                }

            } else {
                System.out.println("No user found");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }


    public String delete(User user) {
        return null;
    }


    public static User update(User user) {
        return null;
    }

}

