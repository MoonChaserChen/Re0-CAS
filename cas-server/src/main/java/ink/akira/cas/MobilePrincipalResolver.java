package ink.akira.cas;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.services.persondir.IPersonAttributeDao;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
public class MobilePrincipalResolver implements PrincipalResolver {
    private JdbcService jdbcService;
    private final PrincipalFactory principalFactory = PrincipalFactoryUtils.newPrincipalFactory();

    @Override
    public Principal resolve(Credential credential, Optional<Principal> principal, Optional<AuthenticationHandler> handler) {
        String email = credential.getId();
        String username = jdbcService.queryUsernameByMobile(email);
        return principalFactory.createPrincipal(username);
    }

    @Override
    public boolean supports(Credential credential) {
        return StringUtils.isNotBlank(credential.getId());
    }

    @Override
    public IPersonAttributeDao getAttributeRepository() {
        return null;
    }
}
