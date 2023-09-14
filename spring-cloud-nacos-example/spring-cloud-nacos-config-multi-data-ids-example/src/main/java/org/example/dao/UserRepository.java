package org.example.dao;

import org.example.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author 郑亚东
 */
public interface UserRepository extends CrudRepository<User,Long> {
}
