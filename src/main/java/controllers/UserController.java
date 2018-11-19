package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import cache.UserCache;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cbsexam.UserEndpoints;
import model.User;
import utils.Hashing;
import utils.Log;


public class UserController {
    //MAIKEN NOTES:
    private static DatabaseController dbCon;
    private String token;

    public UserController() {
        this.dbCon = new DatabaseController();
        this.token = token;

    }

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


        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        // Insert the user in the DB
        // TODO: Hash the user password before saving it : FIXED
        // MAIKEN NOTES:
        int userID = dbCon.insert(
                "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                        + user.getFirstname()
                        + "', '"
                        + user.getLastname()
                        + "', '"
                        + Hashing.sha(user.getPassword())  //MAIKEN NOTES: Hasher passordet i user klassen, og henter deretter passordet som allerede er hashet.
                        + "', '"
                        + user.getEmail()
                        + "', "
                        + user.getCreatedTime()
                        + ")");

        if (userID != 0) {
            //Update the userid of the user before returning
            user.setId(userID);


        } else {
            // Return null if user has not been inserted into database
            return null;
        }

        UserEndpoints.userCache.getUsers(true);

        // Return user
        return user;
    }


    //MAIKEN NOTES: SQL statement for å hente email og hashet passord. Henter token algoritme og oppretter token. Verifiserer også tokenen, og returnerer token direkte.
    public String login(User user) {

        // Write in log that we've reach this step
        Log.writeLog(UserController.class.getName(), user, "Login", 0);

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Build the query for DB
        String sql = "SELECT * FROM user WHERE email='" + user.getEmail() + "' AND password='" + Hashing.sha(user.getPassword()) + "'";
        // Actually do the query
        ResultSet rs = dbCon.query(sql);
        User loginUser = null;

        try {
            // Get first object, since we only have one
            if (rs.next()) {
                loginUser = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));

                //MAIKEN NOTES: KILDE https://github.com/auth0/java-jwt
                //FINN UT HVA HMAC256 ER FOR NOE
                try {
                    Algorithm algorithm = Algorithm.HMAC256("secret");
                    token = JWT.create()
                            //MAIKEN NOTES: LEGGER TIL WITHCLAIM, OG SENDER ID MED DET OBJEKTET AV LOGINUSER MED TOKEN
                            .withIssuer("auth0").withClaim("userId", loginUser.id)
                            .sign(algorithm);
                } catch (JWTCreationException exception) {
                    //Invalid Signing configuration / Couldn't convert Claims.}

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


    //MAIKEN NOTES:
    public boolean delete(String token) {

        // Dekoder token: KILDE https://github.com/auth0/java-jwt, FINN UT HVA DEN GJØR
        DecodedJWT jwt = null;
        try {
            jwt = JWT.decode(token);
        } catch (JWTDecodeException exception) {
            //Invalid token
        }

        // Write in log that we've reach this step
        Log.writeLog(UserController.class.getName(), null, "Delete user by id", 0);

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        String sql = "DELETE FROM user WHERE id=" + jwt.getClaim("userId").asInt();
        // MAIKEN NOTES: Bruger insert, fordi man trenger og bruge executeUpdate, da
        int i = dbCon.insert(sql);

        if (i == 1) {
            return true;
        } else {
            return false;

        }
    }


    //MAIKEN NOTES:
    public static boolean updateUser(User user, String token) {

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Verifiserer token: KILDE https://github.com/auth0/java-jwt, FINN UT HVA DEN GJØR
        DecodedJWT jwt = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("cbsexam")
                    .build(); //Reusable verifier instance
                     jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            exception.printStackTrace();
            //Invalid signature/claims
        }

        String sql = "UPDATE user SET first_name = '" + user.getFirstname() + "', last_name='" + user.getLastname() + "', password ='" + Hashing.sha(user.getPassword()) + "' ,email=,'" + user.getEmail() + " ' WHERE id = " + jwt.getClaim("userId").asInt();

        int j = dbCon.insert(sql);

        if (j == 1) {
            return true;
        } else {
            return false;

        }


    }

}


