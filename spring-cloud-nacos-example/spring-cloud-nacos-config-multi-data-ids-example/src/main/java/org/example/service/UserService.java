package org.example.service;

import org.example.model.User;
import org.springframework.stereotype.Service;

/**
 * @author 郑亚东
 */
@Service
public interface UserService {

    User findById(Long id);

}
