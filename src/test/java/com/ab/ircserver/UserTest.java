package com.ab.ircserver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class UserTest {

    @Test
    public void testIsCorrectPassword() {
        byte[] password = "password".getBytes(StandardCharsets.UTF_8);
        byte[] passwordWrong = "passwore".getBytes(StandardCharsets.UTF_8);
        byte[] passwordCopy = Arrays.copyOf(password, password.length);
        User user = new User("username", password);
        
        assertTrue("Password is correct", user.isCorrectPassword(new String("username"), passwordCopy));
        assertFalse("User name is wrong", user.isCorrectPassword(new String("usernam1"), passwordCopy));
        assertFalse("Password is wrong", user.isCorrectPassword(new String("username"), passwordWrong));
    }

}
