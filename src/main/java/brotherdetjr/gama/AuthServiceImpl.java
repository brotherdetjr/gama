package brotherdetjr.gama;

import com.google.common.collect.ImmutableSet;
import io.javalin.BasicAuthCredentials;
import io.javalin.Context;

import java.util.Set;

import static brotherdetjr.gama.UserRole.GAMER;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

public class AuthServiceImpl implements AuthService {
    @Override
    public String extractUserName(Context ctx) {
        return innerExtractUsername(ctx);
    }

    @Override
    public Set<UserRole> extractRoles(Context ctx) {
        return !isNullOrEmpty(innerExtractUsername(ctx)) ? ImmutableSet.of(GAMER) : emptySet();
    }

    private String innerExtractUsername(Context ctx) {
        return ofNullable(ctx.basicAuthCredentials())
                .map(BasicAuthCredentials::getUsername)
                .orElse(null);
    }
}
