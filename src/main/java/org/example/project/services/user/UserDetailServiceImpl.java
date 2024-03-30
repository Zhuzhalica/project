package org.example.project.services.user;

import lombok.RequiredArgsConstructor;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с таким login не существует."));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), true, true, true,
                true, List.of(user.getRole()));
    }
}