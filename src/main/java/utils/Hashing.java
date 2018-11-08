package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;

public final class Hashing {

  private String salt = null;

  //TILFØY SALT TIL HASHING(ARTIKKEL: BEST PRACTICE, SALT OG HASHING AV USER PASS)
  // TODO: You should add a salt and make this secure : FIXED
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure : FIXED
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      //rawString = rawString + User.getCreatedTime();

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return rawString;
  }

  // Method to hash the password with salt, with a string parameter
  public String saltyHash(String password){
    //Defining the salt string, so it adds the salt string to the password string.
    String salt = password+this.salt;

    //bruk sha i stedet for md5: md5 er en gammel metode, som er lett å knekke, hvis man setter rainbows table så kan man finne det ut på kort tid, bruk deretter sha
    return md5(salt);

  }

  public void setSalt(String salt) {
    this.salt = salt;
  }
}