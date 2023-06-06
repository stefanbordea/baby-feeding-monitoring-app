package gr.athtech.babyfeedingmonitoringapp.service;

import gr.athtech.babyfeedingmonitoringapp.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User findById(Long id);

    List<User> findAll();

    User update(User user);

    void delete(User user);
}
