package gr.athtech.babyfeedingmonitoringapp.repository;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.model.User;

public interface UserRepository extends GenericRepository<User, Long> {

    Role checkUserRole(String username, String password);
}
