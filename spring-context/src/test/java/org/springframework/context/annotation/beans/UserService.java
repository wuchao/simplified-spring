package org.springframework.context.annotation.beans;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String welcome() {
        return "This is UserService.";
    }

}
