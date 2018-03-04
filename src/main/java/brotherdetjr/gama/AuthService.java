package brotherdetjr.gama;

import io.javalin.Context;

import java.util.Set;

public interface AuthService {
    String extractUserName(Context ctx);
    Set<UserRole> extractRoles(Context ctx);
    String getToken(Context ctx);
}
