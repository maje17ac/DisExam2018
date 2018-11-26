package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it : FIXED
public class OrderCache {

    // Liste over ordre
    private ArrayList<Order> orders;

    // Levetid for cachen (time to live)
    private long ttl;

    //Settes når cachen har blit opprettet (altså opprettelses tid)
    private long created;

    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {

        /* Hvis vi ønsker å klarere cachen, kan vi fremtvinge en oppdatering
        Eller, så vil vi se på alderen for cachen og finne ut når vi skal oppdatere.
        Hvis created time + levetid er større enn fastsatt levetid så vil man oppdatere
        Hvis listen er tom, så sjekker vi også for nye produkter, endrer fra isEmpty() til == null, hvis ikke vil den ike hente ordrene */

        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.orders == null) {

            // Henter ordre fra controlleren, siden vi ønsker å oppdatere
            ArrayList<Order> orders = OrderController.getOrders();

            // Setter orders for instansen og setter oppretted tidsstempel
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
        }

        // Returnerer dokumentene
        return this.orders;

    }


}
