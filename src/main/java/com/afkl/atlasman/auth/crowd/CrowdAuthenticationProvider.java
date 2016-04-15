package com.afkl.atlasman.auth.crowd;

import com.atlassian.crowd.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
public class CrowdAuthenticationProvider implements AuthenticationProvider {

    private final Crowd crowd;

    @Autowired
    public CrowdAuthenticationProvider(Crowd crowd) {
        this.crowd = crowd;
    }

    private static boolean isValid(Authentication authentication) {
        return authentication.getCredentials() != null && !authentication.getCredentials()
                .toString().equalsIgnoreCase("n/a");
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        try {
            Optional<User> user = isValid(authentication) ?
                    crowd.authenticate(authentication.getName(),
                            authentication.getCredentials().toString()) :
                    Optional.empty();
            return user.map(this::convert).orElse(null);
        } catch (HttpServerErrorException e) {
            if (!e.getStatusCode().equals(UNAUTHORIZED)) {
                log.warn("Unable to authenticate due to exception.", e);
            }
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    private Authentication convert(User user) {
        return crowd.listGroups(user.getName(), false).map(groups -> {
            List<GrantedAuthority> grantedAuthorities =
                    groups.stream().map(group -> new SimpleGrantedAuthority(group.getName()))
                            .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getName(), "", grantedAuthorities);
            token.setDetails(user);
            log.debug("Successfully authenticated {}.", user.getName());
            return token;
        }).orElse(null);
    }

}

