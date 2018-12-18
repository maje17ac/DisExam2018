package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it : FIXED


public class UserCache {

    // Liste over brukere
    private ArrayList<User> users;

    // Levetid til cachen
    private long ttl;

    // Setter tid til når cachen har blitt opprettet
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {

        /* Hvis vi ønsker å klarere cachen, kan vi fremtvinge en oppdatering
        Eller, så vil vi se på alderen for cachen og finne ut når vi skal oppdatere.
        Hvis created time + levetid er større enn fastsatt levetid så vil man oppdatere
        Hvis listen er tom, så sjekker vi også for nye produkter, endrer fra isEmpty() til == null, hvis ikke vil den ike hente brugerne */

        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.users == null) {

            // Henter brukerne fra controlleren, siden vi ønsker å oppdatere
            ArrayList<User> users = UserController.getUsers();

            // Setter users for instansen og setter opprettet tidsstempel
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;
        }

        // Returnerer dokumentene
        return this.users;

    }


}
