package org.example.project.helpers;

import lombok.RequiredArgsConstructor;
import org.example.project.dataModels.models.User;
import org.example.project.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContextHelper {
    private final UserService userService;

    public User getUserByRequestContext() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getByLogin(userDetails.getUsername());
    }
}
