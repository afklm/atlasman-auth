package com.afkl.atlasman.auth.crowd;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

@Component
@Slf4j
public class Crowd {

    private final CrowdClient crowdClient;

    @Autowired
    public Crowd(CrowdClient crowdClient) {
        this.crowdClient = crowdClient;
    }

    public Optional<List<Group>> listGroups(String username, boolean nested) {
        if (hasText(username)) {
            try {
                return Optional.of(nested ?
                        crowdClient.getGroupsForNestedUser(username, 0, -1) :
                        crowdClient.getGroupsForUser(username, 0, -1));
            } catch (OperationFailedException e) {
                log.warn("Unable to list groups for user {} due to exception -> {}:{}.", username, e.getClass().getName(), e.getMessage());
                throw new HttpServerErrorException(SERVICE_UNAVAILABLE, format("Unable to list groups for user %s.", username));
            } catch (InvalidAuthenticationException | ApplicationPermissionException | UserNotFoundException e) {
                log.warn("Not allowed to list groups for user {} due to exception -> {}:{}.", username, e.getClass().getName(), e.getMessage());
                throw new HttpServerErrorException(UNAUTHORIZED, format("Unable to list groups for user %s.", username));
            }
        }
        return Optional.empty();
    }

    public Optional<User> load(String username) {
        try {
            return Optional.of(crowdClient.getUser(username));
        } catch (UserNotFoundException e) {
            return Optional.empty();
        } catch (OperationFailedException e) {
            log.warn("Unable to authenticate user due to unknown exception.", e);
            throw new HttpServerErrorException(SERVICE_UNAVAILABLE, "Unable to authenticate user due to unknown exception.");
        } catch (InvalidAuthenticationException | ApplicationPermissionException e) {
            throw new HttpServerErrorException(UNAUTHORIZED, "Unable to authenticate with provided credentials.");
        }
    }

    public Optional<User> authenticate(String username, String secret) {
        if (hasText(username) && hasText(secret)) {
            try {
                crowdClient.testConnection();
            } catch (OperationFailedException e) {
                throw new HttpServerErrorException(SERVICE_UNAVAILABLE, "No active connection to crowd available.");
            } catch (InvalidAuthenticationException | ApplicationPermissionException e) {
                throw new HttpServerErrorException(UNAUTHORIZED, "No authorized to open a connection to crowd.");
            }
            try {
                return Optional.of(crowdClient.authenticateUser(username, secret));
            } catch (UserNotFoundException e) {
                return Optional.empty();
            } catch (OperationFailedException e) {
                log.warn("Unable to authenticate user due to unknown exception.", e);
                throw new HttpServerErrorException(SERVICE_UNAVAILABLE, "Unable to authenticate user due to unknown exception.");
            } catch (ExpiredCredentialException | InactiveAccountException e) {
                throw new HttpServerErrorException(LOCKED, "Requested account is locked.");
            } catch (InvalidAuthenticationException | ApplicationPermissionException e) {
                throw new HttpServerErrorException(UNAUTHORIZED, "Unable to authenticate with provided credentials.");
            }
        }
        return Optional.empty();
    }

}
