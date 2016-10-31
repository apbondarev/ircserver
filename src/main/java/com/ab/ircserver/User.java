package com.ab.ircserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User {

    // A random string that is appended to the password before hashing it
    private static final String SALT = System.getProperty("ircserver.password.salt", "nU77fQM43KLTGLVjBe9k");

	public static final User ANONIMOUS = new User("anonimous", "password".getBytes(StandardCharsets.UTF_8));
    
    private final String name;
    private byte[] passwordMd5;

    public User(String name, byte[] password) {
        this.name = name;
        this.passwordMd5 = md5(password);
    }

    private byte[] md5(byte[] password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IrcServerException("MD5", e);
        }
        byte[] saltBytes = SALT.getBytes(StandardCharsets.US_ASCII);
        byte[] input = Arrays.copyOf(password, password.length + saltBytes.length);
        System.arraycopy(saltBytes, 0, input, password.length, saltBytes.length);
        md.update(input);
        return md.digest();
    }

    public boolean isCorrectPassword(String aName, byte[] aPassword) {
        byte[] hash = md5(aPassword);
        return name.equals(aName) && Arrays.equals(passwordMd5, hash);
    }

    public String name() {
        return name;
    }
}
