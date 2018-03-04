package brotherdetjr.gama;

import com.google.common.collect.ImmutableSet;
import io.javalin.BasicAuthCredentials;
import io.javalin.Context;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.util.Set;

import static brotherdetjr.gama.UserRole.GAMER;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static org.bouncycastle.util.encoders.Hex.toHexString;

public class AuthServiceImpl implements AuthService {

    private final long salt;

    public AuthServiceImpl(long salt) {
        this.salt = salt;
    }

    @Override
    public String extractUserName(Context ctx) {
        return innerExtractUsername(ctx);
    }

    @Override
    public Set<UserRole> extractRoles(Context ctx) {
        return !isNullOrEmpty(innerExtractUsername(ctx)) ? ImmutableSet.of(GAMER) : emptySet();
    }

    @Override
    public String getToken(Context ctx) {
        return toHexString(new SHA3.Digest224().digest((innerExtractUsername(ctx) + salt).getBytes()));
    }

    private String innerExtractUsername(Context ctx) {
        return ofNullable(ctx.basicAuthCredentials())
                .map(BasicAuthCredentials::getUsername)
                .orElse(null);
    }
}
