/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package needhamschroeder;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Daehyun Kim
 */
public class User {    
     
   private Key key;
   private Key CAKey;   
   private int nonce;
   private int result;
   private String name;
   private String msgReceived;
   private String receivingServerMsg;
   private String firstDec;
   private static final byte[] keyValue = new byte[]{'a', 'C', 'F', 'd', 'k', 'm', 'z', 'y', 't',
                    'u', 's', 'k', 'A', 'B', 'Y', 'Z'};
   
   public User(String nameIn) {
      name = nameIn;      
   }
   
   public String getName() {
      return name;
   }  
   
   public void setKey() {
      
      key = new SecretKeySpec(keyValue, "AES");
   }
   
   public void sendKey(CentralAuthority CA) {
      CA.receiveKey1(key);
   } 
  
   public void setNonce() {
      nonce = (int)((Math.random() + 1) * 100);
   }
   
   public int getNonce() {
      int current = nonce;
      setNonce();
      return current;        
   }
   
   //User sends message to Central Authority
   // Format: Sender|Receiver|Sender's nonce
   public void sendMessageCA(CentralAuthority CA, String sender, String receiver) {
      String messageToSend;
      String delimiter = "|";
      messageToSend = sender + delimiter + receiver + delimiter 
             + Integer.toString(nonce);
      
      CA.setMessageReceived(messageToSend);
      System.out.println("User sends message to Central Authority:");
      System.out.println(messageToSend + "\n");
   }
   
   //Receives encrypted message back from Central Authority
   public void receiveMsgCA(String str) {
      msgReceived = str;
   }
   
   //Receives encrypted message from Receiving Server
   public void receiveMsgRServer(String str) {
       receivingServerMsg = str;
   }
   
    // decryptes message received from Central Authority
   public void decryptMessage1() throws Exception {
       Cipher decryptC = Cipher.getInstance("AES");
       decryptC.init(Cipher.DECRYPT_MODE, key);
       byte[] decorded = Base64.getDecoder().decode(msgReceived);
       byte[] decVal = decryptC.doFinal(decorded);
       firstDec = new String(decVal);            
       String[] firstMsgs = firstDec.split("\\|");
       byte[] decodedKey = Base64.getDecoder().decode(firstMsgs[3]);
       CAKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
       
       System.out.println("User decrypts the message from Central Authority:");
       System.out.println(firstDec + "\n");
   }
   
   // decrypts message received from Receiving Authority and calculates
   // the function for Receiving Authority's nonce
   public void decryptMessage2() throws Exception {
       Cipher decryptC = Cipher.getInstance("AES");
       decryptC.init(Cipher.DECRYPT_MODE, CAKey);
       byte[] decorded = Base64.getDecoder().decode(receivingServerMsg);
       byte[] decVal = decryptC.doFinal(decorded);
       String decrypted = new String(decVal);
       
       System.out.println("\nUser decrypts the message from Receiving Server:");
       System.out.println(decrypted + "\n");       
       
       String[] receiverMsgs = decrypted.split("\\|");
       int r = Integer.parseInt(receiverMsgs[0]);
       int input;
       Scanner in = new Scanner(System.in);
       System.out.println("r (nonce received): " + r);
       System.out.println("Equation: " + receiverMsgs[1]);
       System.out.print("Answer: ");
       input = in.nextInt();
       result = input;       
       
   }
   
   //Sends encrypted message to Receiving Serveer
   public void sendToReceiver(ReceivingServer RS) {
       String[] strList = firstDec.split("\\|");
       String toSend = strList[4];
       RS.receiveMsg1(toSend);
       
       System.out.println("User sends the message encrypted with Receiving Server's key"
               + " to the Receiving Server");
       System.out.println(toSend + "\n");
   }
   
   //Sends the answer for the f(Receiving Server's nonce) to Receiving server
   public void sendResultToReceiver(ReceivingServer RS) throws Exception {
       String msg = Integer.toString(result);
       Cipher cipher = Cipher.getInstance("AES");
       cipher.init(Cipher.ENCRYPT_MODE, CAKey);
       byte[] encode = cipher.doFinal(msg.getBytes());
       byte[] encVal = Base64.getEncoder().encode(encode);
       String toSend = new String(encVal);
       
       System.out.println("\nUser sends the result of the function encrypted with"
               + "the Central Authority's key back to Receiving Server:");
       System.out.println(toSend + "\n");
       
       RS.receiveResult(toSend);
   }
   
       
   
   
}
