package com.chrjo.tradeking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SignatureException;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

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
	      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); 
	      conn.setDoInput (true); 
	      conn.setDoOutput (true); 
	      conn.setUseCaches (false); 
	      conn.setRequestProperty("Content-Type", "application/xml"); 
	      conn.setRequestProperty("Accept", "application/json"); 
	      conn.setRequestProperty("TKI_TIMESTAMP", timestamp); 
	      conn.setRequestProperty("TKI_SIGNATURE", signature); 
	      conn.setRequestProperty("TKI_USERKEY", userKey); 
	      conn.setRequestProperty("TKI_APPKEY", appKey); 
	      DataOutputStream out = new DataOutputStream (conn.getOutputStream()); 
	      out.writeBytes(body); 
	      out.flush(); 
	      out.close();
	      BufferedReader in = new BufferedReader (new 
	      InputStreamReader(conn.getInputStream ())); 
	      String temp; 
	      while ((temp = in.readLine()) != null){ 
	        response += temp + "\n"; 
	      } 
	      temp = null; 
	      in.close(); 
	      conn.disconnect();
	      return response; 
	    } 
	    catch(java.security.SignatureException e) { 
	      return "Signature Error: " + e; 
	    } 
	    catch(java.io.IOException e) { 
	      return "IO Error: " + e; 
	    } 
	  }
  
  	  public String run(){
  		String appKey = keys.appKey;
	    String userKey = keys.userKey;
	    String userSecret = keys.userSecret;
	    String accountNumber = keys.accountNumber;
	    String body = 
	    	"<request><account>" + accountNumber + "</account></request>";
  		return TradeKingAPI.request("https://tkapi.tradeking.com/beta/account/holdings", body, appKey, userKey, userSecret);
  	  }
}
