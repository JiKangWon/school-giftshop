package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


public class Encryption {
	public static String toSHA1(String str) {
		String salt = "aeuhd@yugod%kljfoig84308hgoueryt3^&4hgiry";
		String result = null;
		
		str += salt;
		try {
			byte[] dataBytes = str.getBytes(StandardCharsets.UTF_8);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(dataBytes);
			result = Base64.getEncoder().encodeToString(digest);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String toSHA256(String str) {
		String salt = "aeuhd@yugod%kljfoig84308hgoueryt3^&4hgiry";
		String result = null;
		
		str += salt;
		try {
			byte[] dataBytes = str.getBytes(StandardCharsets.UTF_8);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(dataBytes);
			result = Base64.getEncoder().encodeToString(digest);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void main(String[] args) {
		System.out.println(toSHA256("123"));
	}
}
