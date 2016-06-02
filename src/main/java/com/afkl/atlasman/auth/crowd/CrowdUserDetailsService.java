package com.afkl.atlasman.auth.crowd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CrowdUserDetailsService implements UserDetailsService {

    private final Crowd crowd;

    @Autowired
    public CrowdUserDetailsService(Crowd crowd) {
        this.crowd = crowd;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return crowd.load(username).map(u -> {
            List<SimpleGrantedAuthority> grantedAuthorities =
                    crowd.listGroups(username, false)
                            .map(g -> g.stream()
                                    .map(group -> new SimpleGrantedAuthority(group.getName()))
                                    .collect(toList()))
                            .orElse(new ArrayList<>());
            return new User(u.getName(), "", grantedAuthorities);
        }).orElse(null);
    }

}
