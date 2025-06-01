package com.gitittogether.skillForge.server.config;

import com.gitittogether.skillForge.server.model.user.User;
import com.gitittogether.skillForge.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user details by user ID.
     * This is used for JWT authentication where we need to fetch user details based on the user ID extracted from the token.
     *
     * @param userId The ID of the user to load.
     * @return UserDetails object containing user information.
     */
    public UserDetails loadUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }

    /**
     * Load user details by username.
     * This is used for standard authentication where we need to fetch user details based on the username.
     *
     * @param username The username of the user to load.
     * @return UserDetails object containing user information.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }
}