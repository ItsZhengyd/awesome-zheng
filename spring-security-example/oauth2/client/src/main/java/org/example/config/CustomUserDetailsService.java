package org.example.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.entity.CustomUser;
import org.example.repository.CustomUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomUserRepository userRepository;

    private final Cache<String, CustomUser> cacheCustomUser;

    public CustomUserDetailsService(CustomUserRepository userRepository, Cache<String, CustomUser> cacheCustomUser) {
        this.userRepository = userRepository;
        this.cacheCustomUser = cacheCustomUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser customUser = cacheCustomUser.get(username, key -> this.userRepository.findByName(username));
        if (customUser == null) {
            throw new UsernameNotFoundException("username " + username + " is not found");
        }
        return new CustomUserDetails(customUser);
    }


    static final class CustomUserDetails extends CustomUser implements UserDetails {

        private static final List<GrantedAuthority> ROLE_USER = Collections
                .unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

        CustomUserDetails(CustomUser customUser) {
            super(customUser.getId(), customUser.getName(), customUser.getEmail(),customUser.getPassword());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return ROLE_USER;
        }

        @Override
        public String getUsername() {
            return getName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }
}
