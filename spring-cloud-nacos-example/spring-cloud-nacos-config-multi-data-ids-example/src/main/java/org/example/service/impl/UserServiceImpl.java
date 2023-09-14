package org.example.service.impl;

import org.example.dao.UserRepository;
import org.example.model.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 郑亚东
 */
@Service
@RefreshScope
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final RedisTemplate redisTemplate;

    @Value("${app.user.cache}")
    private boolean cache;

    public UserServiceImpl(UserRepository userRepository, RedisTemplate redisTemplate){
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User findById(Long id) {
        LOGGER.info("cache: {}", cache);

        if(cache) {
            Object obj = redisTemplate.opsForValue().get(key(id));
            if (obj!=null) {
                LOGGER.info("get user from cache, id: {}",id);
                return (User) obj;
            }
        }

        User user = userRepository.findById(id).orElse(null);
        if (user!=null) {
            if(cache) {
                LOGGER.info("set cache for user, id: {}",id);
                redisTemplate.opsForValue().set(key(id),user);
            }
        }

        return user;
    }

    private String key(Long id) {
        return String.format("nacos-spring-cloud-config-multi-data-ids-example:user:%d", id);
    }

}
