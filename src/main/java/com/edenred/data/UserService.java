package com.edenred.data;

import com.edenred.data.EntityNotFoundException.UserNotFoundException;
import com.edenred.order.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired UserRepository userRepository;
    @Autowired DtoMapper dtoMapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException( String email ) {
            super( String.format( "User with email %s already exists", email ) );
        }
    }

    public Optional<User> get( String email ) {
        return userRepository.findByEmail( email )
                .map( dtoMapper::userToDto );
    }

    public User create( String name, String email ) {
        Optional<UserEntity> existing = userRepository.findByEmail( email );
        if (existing.isPresent())
            throw new UserAlreadyExistsException( email );
        UserEntity user = userRepository.save( new UserEntity( name, email ) );
        return dtoMapper.userToDto( user );
    }

    UserEntity getUserEntity( long userId ) {
        return userRepository.findById( userId )
                .orElseThrow( () -> new UserNotFoundException( userId ) );
    }
}
