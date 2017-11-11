/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package needhamschroeder;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author sky45611
 */
public class ReceivingServer {
    
   private Key key;  
   private Key CAKey;
   private int nonce;
   private int answer;
   private int result;
   private String name;
   private String msg1;   
   private String firstDec;
   private static final byte[] keyValue = new byte[]{'a', 'C', 'F', 'd', 'k', 'm', 'z', 'y', 't',
                    'u', 's', 'k', 'A', 'B', 'Y', 'Z'};
   
   public ReceivingServer(String nameIn) {
       name = nameIn;
   }
   
   public String getName() {
       return name;
   }
   public void setKey() {      
      key = new SecretKeySpec(keyValue, "AES");
   }
   
   public void sendKey(CentralAuthority CA) {
      CA.receiveKey2(key);
   }
   
   public void setNonce() {
      nonce = (int)((Math.random() + 1) * 100);
      answer = (3 * nonce) - 3;
   }
   
   public void receiveMsg1(String str) {
       msg1 = str;
   }
   
   // decrypt the message received from user
   public void decryptMsg1() throws Exception {
       Cipher decryptC = Cipher.getInstance("AES");  
       decryptC.init(Cipher.DECRYPT_MODE, key);       
       byte[] decordedVal = Base64.getDecoder().decode(msg1);
       byte[] decoded = decryptC.doFinal(decordedVal);
       firstDec = new String(decoded);
       String[] result = firstDec.split("\\|");       
       byte[] decodedKey = Base64.getDecoder().decode(result[1]);
       CAKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");      
       
       //DEBUG
       System.out.println("Receiving Server decrypts the message from user: " 
               + firstDec + "\n");      
   }
   
   // send the function f(nonce) encrypted with CA's key
   public void sendToUser(User user) throws Exception {
       String in = Integer.toString(nonce);
       String func = "3r - 3";
       String delimiter = "|";
       String toEncode = in + delimiter + func;
       Cipher cipher = Cipher.getInstance("AES");
       cipher.init(Cipher.ENCRYPT_MODE, CAKey);
       byte[] encode = cipher.doFinal(toEncode.getBytes());
       byte[] encVal = Base64.getEncoder().encode(encode);
       String toSend = new String(encVal);       
       
       user.receiveMsgRServer(toSend);       
       
       System.out.println("Receiving Server sends user the equation to solve using nonce and the"
               + " nonce itself encrypted with Central Authority's key:");
       System.out.println(toSend + "\n");
   }
   
   // receive the result of the function encrypted with the
   // CA's key from the user
   public void receiveResult(String str) throws Exception {
       Cipher decryptC = Cipher.getInstance("AES");
       decryptC.init(Cipher.DECRYPT_MODE, CAKey);
       byte[] decordedVal = Base64.getDecoder().decode(str);
       byte[] decoded = decryptC.doFinal(decordedVal);
       String decrypted = new String(decoded);
       result = Integer.parseInt(decrypted);
       
       System.out.println("Receiving Server decrypts the message from user:");
       System.out.println(decrypted + "\n");
   }
   
   // check if the result for the function is correct
   public boolean check() {
       if (result == answer) {
           return true;
       }
       return false;
   }
   
   //if the result is correct, then the receiving server can now trust the user
   public void trustWorthy(User user) {
       if (check() == true) {
           System.out.println(user.getName() + " is trustworthy!");
       }
       else {
           System.out.println(user.getName() + " not trustworthy!");
       }
   }
}
