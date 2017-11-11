/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package needhamschroeder;
import java.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


/**
 *
 * @author Daehyun Kim
 */
public class CentralAuthority {
   
   private String[] userMessage;
   private Key key1;
   private Key key2;
   private Key sessionKey;
   private Cipher cipher;   
   private String message;
   private int nonce1;
   private static final byte[] keyValue = new byte[]{'a', 'C', 'F', 'd', 'k', 'm', 'z', 'y', 't',
                    'u', 's', 'k', 'A', 'B', 'Y', 'Z'};
      
   public void setMessageReceived(String message) {
      userMessage = message.split("\\|");
   }
   
   public void setSessionKey() {            
      sessionKey = new SecretKeySpec(keyValue, "AES");
   }  
   
   public void receiveKey1(Key key) {
      key1 = key;
   }

   public void receiveKey2(Key key) {
      key2 = key;
   }
   
   //Encrypts the message sent by the user
   public void encrypt() throws Exception {
      //Create message to be encrypted with the receiving server's key
      String delimiter = "|";
      String temp1 = userMessage[0] + delimiter + Base64.getEncoder().encodeToString(sessionKey.getEncoded());      
      
      //get User's nonce
      nonce1 = Integer.parseInt(userMessage[2]);
      
      //Encrypt the message created above with the receiving server's key
      cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, key2);
      byte[] encode1 = cipher.doFinal(temp1.getBytes());
      byte[] encVal1 = Base64.getEncoder().encode(encode1);
      String toBeEncrypted = new String(encVal1);
      
      //Encrypt the rest of the message with the one we just encrypted
      String temp2 = userMessage[0] + delimiter +userMessage[1] + delimiter +
            nonce1 + delimiter + Base64.getEncoder().encodeToString(sessionKey.getEncoded())
              + delimiter + toBeEncrypted;      
      cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, key1);      
      byte[] encode2 = cipher.doFinal(temp2.getBytes());     
      byte[] encVal2 = Base64.getEncoder().encode(encode2);
      String encrypted = new String(encVal2);
      message = encrypted;
      
      System.out.println("CA encrypts the message to send back to the user:");
      System.out.println(message + "\n");
   }
   
   // send the encrypted message back to the user
   public void sendMessage(User user) {
      user.receiveMsgCA(message);     
      
   }  
   
   
}   
