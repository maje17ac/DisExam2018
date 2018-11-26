package utils;

import sun.awt.ConstrainableGraphics;

public final class Encryption {

    public static String encryptDecryptXOR(String rawString) {

        // If encryption is enabled in Config.
        if (Config.getEncryption()) {

            // The key is now more complex in config.json, and created in Config
            // TODO: Create a more complex code and store it somewhere better : FIXED
            char[] key = Config.getEncryptionKey();

            // Stringbuilder enables you to play around with strings and make useful stuff
            StringBuilder thisIsEncrypted = new StringBuilder();

            // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
            //MAIKEN NOTE
            for (int i = 0; i < rawString.length(); i++) {
                thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length]));
            }

            // We return the encrypted string
            return thisIsEncrypted.toString();

        } else {
            // We return without having done anything
            return rawString;
        }
    }

}
