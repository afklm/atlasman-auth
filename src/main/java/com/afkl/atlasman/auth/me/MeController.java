package com.afkl.atlasman.auth.me;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.Callable;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Configuration
@RestController
@EnableResourceServer
public class MeController {

    @RequestMapping(value = "/user", method = GET)
    public Callable<Principal> getUser(Principal user) {
        return () -> user;
    }

}
