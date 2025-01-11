package ace.charitan.donation.internal.auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUtils {
    public static AuthModel getUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            return (AuthModel) authentication.getPrincipal();
        } catch (ClassCastException | AuthenticationCredentialsNotFoundException e) {
            return null;
        }
    }

}
