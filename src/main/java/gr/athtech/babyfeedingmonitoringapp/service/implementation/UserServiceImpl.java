package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.UserRepository;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
