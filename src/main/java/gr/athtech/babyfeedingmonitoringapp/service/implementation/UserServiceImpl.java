package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.UserRepository;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

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
