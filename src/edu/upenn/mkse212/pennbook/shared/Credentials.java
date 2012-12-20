package edu.upenn.mkse212.pennbook.shared;

import java.io.UnsupportedEncodingException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

/*class used to hash passwords with randomly generated salts; generate random unique userIds*/
public class Credentials {
	private String userId;
	private String salt;
	private String passwordHash;
	
	public Credentials(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		this.salt = generateSalt();
		this.passwordHash = hashPassword(password,this.salt);
		this.userId = UUID.randomUUID().toString(); //create a UUID as the user Id
	}
	
	
	//SHA-256 hash string of string
	public static String hashString(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		byte[] strBytes = str.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		final byte[] resultBytes = md.digest(strBytes);
		BigInteger bigInt = new BigInteger(1,resultBytes);
		String hash = bigInt.toString(16);
		return hash;
	}

	//random 8 characher salt
	public static String generateSalt()
	{
		return RandomStringUtils.random(8);
	}
	
	public static String hashPassword(String password, String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return hashString(password+salt);
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public String getUserId() {
		return userId;
	}

}