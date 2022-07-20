package dev.bithole.siphon.core;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;

import java.util.List;

// super dodgy hack; we attach the Client associated w/ the request to the HttpServerExchange with a custom SecurityContext
// this is absolutely disgusting... need to figure out a better way to do this, but it's 12:16 AM so whatever
public class SiphonSecurityContext implements SecurityContext {

    private final Client client;

    public SiphonSecurityContext(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean authenticate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean login(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthenticationRequired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthenticationRequired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AuthenticationMechanism> getAuthenticationMechanisms() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Account getAuthenticatedAccount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMechanismName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IdentityManager getIdentityManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void authenticationComplete(Account account, String s, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void authenticationFailed(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerNotificationReceiver(NotificationReceiver notificationReceiver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotificationReceiver(NotificationReceiver notificationReceiver) {
        throw new UnsupportedOperationException();
    }

}
