/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package needhamschroeder;

import java.util.Arrays;

/**
 *
 * @author Daehyun Kim
 */
public class NeedhamSchroeder {

    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) throws Exception {        
        //Create 1 user, 1 receiving server, and 1 Central Authority
      User alice = new User("Alice");
      ReceivingServer bob = new ReceivingServer("Bob");
      CentralAuthority CA = new CentralAuthority();
       
       //Create nonces and keys for the user and the receiving server
      alice.setNonce();
      bob.setNonce();  
      alice.setKey();;
      bob.setKey();
      
      //Send the users' keys to the Central Authority
      alice.sendKey(CA);
      bob.sendKey(CA);
      
       //Central Authority creates session key
      CA.setSessionKey();      
       
       //User 1 sends a message to Central Authority 
      alice.sendMessageCA(CA, alice.getName(), bob.getName());
       
       //Central authority encrypts information from the message received and 
       //sends another message back to user1, or Alice
      CA.encrypt();
      CA.sendMessage(alice);
      
      //Decrypt message with alice's key
      alice.decryptMessage1();
      
      //send message encrypted with the receiving server's key to the server
      alice.sendToReceiver(bob);
      
      //Receiving server decrypts the message
      bob.decryptMsg1();
      
      //Receiving serever sends second message to user
      // this contains f(nonce) encrypted with CA's key
      bob.sendToUser(alice);
      
      //User decrypts the second message and calculates
      // the answer for the function
      alice.decryptMessage2();
      
      //User sends the result to the receiving server
      alice.sendResultToReceiver(bob);    
      
      //Receiving server determines if the user is trustworthy or not
      bob.trustWorthy(alice);
      
      
   }
}
    

