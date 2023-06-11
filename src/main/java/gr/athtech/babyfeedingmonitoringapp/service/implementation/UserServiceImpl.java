package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.dto.AuthenticationRequest;
import gr.athtech.babyfeedingmonitoringapp.dto.UserDto;
import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.UserRepository;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

    @Override
    public UserDto registerAdmin(AuthenticationRequest authenticationRequest) {
        if (userRepository.findByUsername(authenticationRequest.getUsername().trim()).isEmpty()) {
            User user = new User();
            user.setUsername(authenticationRequest.getUsername().trim());
            user.setPassword(authenticationRequest.getPassword().trim());
            user.setRole(Role.ADMIN);
            userRepository.create(user);
            UserDto userDto = new UserDto();
            userDto.setUsername(user.getUsername());
            userDto.setId(user.getId());
            userDto.setRole(user.getRole());
            return userDto;
        }
        return null;
    }

    @Override
    public UserDto register(AuthenticationRequest authenticationRequest) {
        if (userRepository.findByUsername(authenticationRequest.getUsername().trim()).isEmpty()) {
            User user = new User();
            user.setUsername(authenticationRequest.getUsername().trim());
            user.setPassword(authenticationRequest.getPassword().trim());
            user.setRole(Role.PHYSICIAN);
            userRepository.create(user);
            UserDto userDto = new UserDto();
            userDto.setUsername(user.getUsername());
            userDto.setId(user.getId());
            userDto.setRole(user.getRole());
            return userDto;
        }
        return null;
    }

    @Override
    public UserDto login(AuthenticationRequest authenticationRequest) {
        Optional<User> user = userRepository.findByUsername(authenticationRequest.getUsername().trim());
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        if (authenticationRequest.getPassword().trim().equals(user.get().getPassword())) {
            UserDto userDto = new UserDto();
            userDto.setUsername(user.get().getUsername());
            userDto.setId(user.get().getId());
            userDto.setRole(user.get().getRole());
            return userDto;
        }
        return null;
    }

    @Override
    public Role checkUserRole(String username, String password) {
        return userRepository.checkUserRole(username, password);
    }

    @Override
    public Optional<User> create(User user) {
        return userRepository.create(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> update(User user) {
        return userRepository.update(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
