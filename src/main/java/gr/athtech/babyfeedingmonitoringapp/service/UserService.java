package gr.athtech.babyfeedingmonitoringapp.service;

import gr.athtech.babyfeedingmonitoringapp.dto.AuthenticationRequest;
import gr.athtech.babyfeedingmonitoringapp.dto.UserDto;
import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> create(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    Optional<User> update(User user);

    void delete(User user);

    Role checkUserRole(String username, String password);

    UserDto register(AuthenticationRequest authenticationRequest);

    UserDto registerAdmin(AuthenticationRequest authenticationRequest);

    UserDto login(AuthenticationRequest authenticationRequest);
}
