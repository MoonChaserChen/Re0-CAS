package ink.akira.cas.config;

import ink.akira.cas.EmailPrincipalResolver;
import ink.akira.cas.JdbcService;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.resolvers.EchoingPrincipalResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;

@Configuration
public class BeanConfiguration {
    public static final String EMAIL_AUTHENTICATION_HANDLER = "queryDatabaseByEmailAuthenticationHandler";

    @Bean
    public JdbcService jdbcService(DataSource dataSource) {
        return new JdbcService(dataSource);
    }

    @Bean
    public PrincipalResolver emailPrincipalResolver(JdbcService jdbcService) {
        return new EmailPrincipalResolver(jdbcService);
    }

    @Bean
    public AuthenticationEventExecutionPlanConfigurer jdbcAuthenticationEventExecutionPlanConfigurer(
            @Qualifier("jdbcAuthenticationHandlers") final Collection<AuthenticationHandler> jdbcAuthenticationHandlers,
            @Qualifier("emailPrincipalResolver") final PrincipalResolver emailPrincipalResolver) {
        return plan -> jdbcAuthenticationHandlers.forEach(h -> {
                    PrincipalResolver principalResolver = StringUtils.equals(EMAIL_AUTHENTICATION_HANDLER, h.getName()) ? emailPrincipalResolver : new EchoingPrincipalResolver();
                    plan.registerAuthenticationHandlerWithPrincipalResolver(h, principalResolver);
                }
        );
    }
}
