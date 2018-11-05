package cache;

import controllers.ProductController;
import java.util.ArrayList;
import model.Product;
import utils.Config;
// DETTE ER EN MÅTE Å LAGE CACHE PÅ, MAN KAN BENYTTE ANDRE MÅTER, TENK PÅ Å BRUKE DE TRE CACHEKLASSER ULIKT ELLER LIKT??
// DE SKAL OGSÅ BRUKES, SÅ SØRG FOR AT DET BLIR GJORT.
// UTFORDRINGER M. CACHING: 1)FORELDET DATA I VÅRES CASH 2) IKKE HAR DET NYESTE DATA I VÅRES CASH 3) VI CACHER NOE SOM VI IKKE FÅR EN VERDI AV Å CASHE, DET VIL TA LENGER TID ALTSÅ MAN KAN FORSINKE SYSTEMET
//SKAL IMPLEMENTERE CACHING FOR PRODUKTER
public class ProductCache {

  // List of products
  private ArrayList<Product> products;

  // Time cache should live
  private long ttl;

  // Sets when the cache has been created
  private long created;

  public ProductCache() {
    this.ttl = Config.getProductTtl();
  }

  public ArrayList<Product> getProducts(Boolean forceUpdate) {

    // If we wish to clear cache, we can set force update.
    // Otherwise we look at the age of the cache and figure out if we should update.
    // If the list is empty we also check for new products
    // MAIKEN NOTES: ENDRET FRA >= til <=

    if (forceUpdate
        || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
        || this.products.isEmpty()) {

      // Get products from controller, since we wish to update.
      ArrayList<Product> products = ProductController.getProducts();

      // Set products for the instance and set created timestamp
      this.products = products;
      this.created = System.currentTimeMillis() / 1000L;
    }

    // Return the documents
    return this.products;
  }
}
