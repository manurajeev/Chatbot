package com.webapp.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;

import com.webapp.baseLibrary.FunctionsLibrary;


/**
 * EncryptFileData.java
 * 
 * @Purpose Encrypt and Decrypt the critical data in properties file
 *
 * @author Prabhuling Kalshetti
 *
 */

public class EncryptFileData extends FunctionsLibrary{

	public static SecretKeySpec secretKey=null;

	/**
	 * Generate Secrete Key which will be used for Encrypt and Decrypt the data
	 * @author Prabhuling Kalshetti
	 *
	 */
	
    public SecretKeySpec createSecretKey(char[] password, byte[] bhaKeyType, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, bhaKeyType, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
	 * With help of Cryptographic Cipher encryption using Java Cryptographic Extension framework(JSE). Encrypting the data with 'AES/CBC/PKCS5Padding'. 
	 * @author Prabhuling Kalshetti
	 *
	 */
    public  String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    public String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
   	 * Decrypting the password with referring common and already generated SecreteKeySpec instance which is deserialized from .ser file.
   	 * @author Prabhuling Kalshetti
   	 *
   	 */
    public  String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    public  byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
    
    /**
   	 * Serialize the SecreteKeySpec object to the path 'src/test/resources/config/' with file name Key.ser
   	 * @author Prabhuling Kalshetti
   	 *
   	 */
   public void serializeKeys(){
    	
    	 try {
    		 FileOutputStream fileOut = new FileOutputStream("src/test/resources/config/Key.ser");
    	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
    	         out.writeObject(secretKey);
    	         out.close();
    	         fileOut.close();
    	    }catch(IOException i) {
             i.printStackTrace();
             return;
          }
    	
    }
   
   /**
  	 * Deserialize the SecreteKeySpec object given at 'src/test/resources/config/' with file name Key.ser
  	 * @author Prabhuling Kalshetti
  	 *
  	 */
    public SecretKeySpec deserializeKey(){
    	SecretKeySpec secKey =null;
    	try{
    		
    		FileInputStream fileIn = new FileInputStream("src/test/resources/config/Key.ser");
    		ObjectInputStream objInput = new ObjectInputStream(fileIn);
    		 secKey = (SecretKeySpec) objInput.readObject();
    		objInput.close();
            fileIn.close();
    	return secKey;   		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return secKey; 
    }
    
    
    
    //Under API
	public void encryptConfigFileData(String dataTOEncrypt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	EncryptFileData ecryptData = new EncryptFileData();
    	String randomStringForKey = RandomStringUtils.randomAlphabetic(10).toUpperCase();
    	System.setProperty(dataTOEncrypt,randomStringForKey);
        String password = System.getProperty(dataTOEncrypt);
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }
        try{
        // The salt (probably) can be stored along with the encrypted data
        byte[] salt = new String(RandomStringUtils.randomNumeric(8)).getBytes();

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
  
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        EncryptFileData.secretKey = ecryptData.createSecretKey(System.getProperty(dataTOEncrypt).toCharArray(),salt, iterationCount, keyLength);
                        
        // File f = new File("src/test/resources/config/config.properties");
        FileInputStream in = new FileInputStream("src/test/resources/config/config.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();

		FileOutputStream out = new FileOutputStream("src/test/resources/config/config.properties");
        
		 FileInputStream inSys = new FileInputStream("Config/Sys.properties");
			Properties propsSys = new Properties();
			propsSys.load(inSys);
			inSys.close();
		
		
		String [] environments = propsSys.getProperty("ExecutionEnvironments").split(",");
       // FileOutputStream outFile = new FileOutputStream("src/test/resources/config/config.properties");
      // Properties prop = new runnerTest().setEnv();
	
		for(String env:environments){
        String originalPassword = props.getProperty(dataTOEncrypt+"_"+env);
        //System.out.println("Original password: " + originalPassword);
        String encryptedPassword = null;
		try {
			encryptedPassword = ecryptData.encrypt(originalPassword, EncryptFileData.secretKey);
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//props.store( out, "First line1" );
        props.setProperty(dataTOEncrypt+"_"+env,encryptedPassword);
        props.setProperty("isFileDataEncrypted_"+env, "true");
        //System.out.println("Encrypted password: " + encryptedPassword);
        String decryptedPassword = null;
		try {
			decryptedPassword = ecryptData.decrypt(encryptedPassword, EncryptFileData.secretKey);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //System.out.println("Decrypted password: " + decryptedPassword);
        
    	
		}
		props.store(out, null);
    	out.close();
		
	} catch (IOException io) {
		io.printStackTrace();
	}
  }
}
