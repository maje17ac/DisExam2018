package utils;

import sun.awt.ConstrainableGraphics;

public final class Encryption {

    public static String encryptDecryptXOR(String rawString) {

        // If encryption is enabled in Config.
        if (Config.getEncryption()) {

            //MAIKEN NOTES: Nøkkelen er nå mer kompleks, og gemt i config
            // TODO: Create a more complex code and store it somewhere better : FIXED
            char[] key = Config.getEncryptionKey();

            // Stringbuilder enables you to play around with strings and make useful stuff
            StringBuilder thisIsEncrypted = new StringBuilder();

            // ^binær operator, gjør det om til binære verdier
            // i er en tellevariabel og sjekker lenge på stringen, og looper gjennom hver bokstav,
            // og endrer det til dere binære verdi, ut i fra våres encryption key
            // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
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
