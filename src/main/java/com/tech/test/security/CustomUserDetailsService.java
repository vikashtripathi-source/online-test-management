package com.tech.test.security;

import com.tech.test.exception.StudentNotFoundException;
import com.tech.test.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return studentRepository.findFirstByEmail(email)
                .map(student -> User.builder()
                        .username(student.getEmail())
                        .password(student.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")))
                        .build())
                .orElseThrow(() -> new StudentNotFoundException("No account found with email '" + email + "'. Please check your email or register for a new account."));
    }
}
