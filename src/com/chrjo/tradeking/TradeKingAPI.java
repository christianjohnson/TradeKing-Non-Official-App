package com.chrjo.tradeking;

import java.security.SignatureException; 
import java.util.Calendar; 
import java.net.URL; 
import java.net.URLConnection; 
import java.io.DataOutputStream; 
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import javax.crypto.Mac; 
import javax.crypto.spec.SecretKeySpec; 

import org.apache.commons.codec.binary.Hex; 
import org.apache.commons.codec.binary.Base64; 

public class TradeKingAPI { 
  public static String generateSignature(String data, String key) throws java.security.SignatureException { 
    String result; 
    try { 
      byte[] encodedData = Base64.encodeBase64(data.getBytes()); 
      // Get an hmac_md5 key from the raw key bytes
      byte[] keyBytes = key.getBytes(); 
      SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5"); 
      // Get an hmac_md5 Mac instance and initialize with the signing key 
      Mac mac = Mac.getInstance("HmacMD5"); 
      mac.init(signingKey); 
      // Compute the hmac on input data bytes 
      byte[] rawHmac = mac.doFinal(encodedData); 
      // Convert raw bytes to Hex 
      byte[] hexBytes = new Hex().encode(rawHmac); 
      //  Covert array of Hex bytes to a String 
      result = new String(hexBytes, "ISO-8859-1"); 
    } 
    catch (Exception e) { 
      throw new SignatureException("Failed to generate HMAC : " + e.getMessage()); 
    } 
    return result; 
  }
  
  public static String request(String resourceUrl, String body, String appKey, 
		  					   String userKey, String userSecret){ 
	    String response = new String(); 
	    try { 
	      String timestamp    = String.valueOf(Calendar.getInstance().getTimeInMillis()); 
	      String request_data = body + timestamp; 
	      String signature = TradeKingAPI.generateSignature(request_data, userSecret); 
	      URL url = new URL(resourceUrl); 
	      URLConnection conn = url.openConnection(); 
	      conn.setDoInput (true); 
	      conn.setDoOutput (true); 
	      conn.setUseCaches (false); 
	      conn.setRequestProperty("Content-Type", "application/xml"); 
	      conn.setRequestProperty("Accept", "application/xml"); 
	      conn.setRequestProperty("TKI_TIMESTAMP", timestamp); 
	      conn.setRequestProperty("TKI_SIGNATURE", signature); 
	      conn.setRequestProperty("TKI_USERKEY", userKey); 
	      conn.setRequestProperty("TKI_APPKEY", appKey); 
	      DataOutputStream out = new DataOutputStream (conn.getOutputStream ()); 
	      out.writeBytes(body); 
	      out.flush(); 
	      out.close(); 
	      BufferedReader in = new BufferedReader (new InputStreamReader(conn.getInputStream ())); 
	      String temp; 
	      while ((temp = in.readLine()) != null){ 
	        response += temp + "\n"; 
	      } 
	      temp = null; 
	      in.close(); 
	      return response; 
	    } 
	    catch(java.security.SignatureException e) { 
	      System.out.println("Error " + e); 
	      return ""; 
	    } 
	    catch(java.io.IOException e) { 
	      System.out.println("Error " + e); 
	      return ""; 
	    } 
	  } 
  
	  public static void main(String[] args) { 
	    String appKey    = "679d0e393f523e6d7b435aba3b2ebe33"; 
	    String userKey    = "476a90129381a6454a3f265f753f624d"; 
	    String userSecret   = "25a3fb239b20f94f76efa254936f626e"; 
	    
	    String body     = 
	    	"<request><account>12345678</account><quote><watchlist>DEFAULT</watchlist><delayed>true</delayed></quote></request>"; 
	    	    String response = TradeKingAPI.request("https://<url>/beta/trade/quotes", body, 
	    	appKey, userKey, userSecret); 
	    	    System.out.println("Server response:\n'" + response + "'"); 
	  } 
}
