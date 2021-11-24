package com.edenred.data;

import com.edenred.data.EntityNotFoundException.UserNotFoundException;
import com.edenred.data.UserService.UserAlreadyExistsException;
import com.edenred.order.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired UserService userService;

    @Test
    public void testUserNotFound() {
        assertThrows( UserNotFoundException.class,
                () -> userService.getUserEntity( 13333 ) );
    }

    @Test
    public void testCreateAndDto() {
        User user = userService.create( "name", "a@b.com" );
        assertEquals( user.getName(), "name" );
        assertEquals( user.getEmail(), "a@b.com" );
        assertTrue( user.getId() > 0 );
    }

    @Test
    public void testDuplicate() {
        User user = userService.create( "name", "a@b.com" );
        assertThrows( UserAlreadyExistsException.class,
                () -> userService.create( "name", "a@b.com" ) );
    }
}