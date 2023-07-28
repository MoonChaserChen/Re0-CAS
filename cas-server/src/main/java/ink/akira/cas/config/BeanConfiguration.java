package ink.akira.cas.config;

import ink.akira.cas.EmailPrincipalResolver;
import ink.akira.cas.JdbcService;
import ink.akira.cas.MobilePrincipalResolver;
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
    public static final String EMAIL_AUTHENTICATION_HANDLER = "emailPwdAuthenticationHandler";
    public static final String MOBILE_AUTHENTICATION_HANDLER = "mobilePwdAuthenticationHandler";

    @Bean
    public JdbcService jdbcService(DataSource dataSource) {
        return new JdbcService(dataSource);
    }

    @Bean
    public PrincipalResolver emailPrincipalResolver(JdbcService jdbcService) {
        return new EmailPrincipalResolver(jdbcService);
    }

    @Bean
    public PrincipalResolver mobilePrincipalResolver(JdbcService jdbcService) {
        return new MobilePrincipalResolver(jdbcService);
    }

    @Bean
    public AuthenticationEventExecutionPlanConfigurer jdbcAuthenticationEventExecutionPlanConfigurer(
            @Qualifier("jdbcAuthenticationHandlers") final Collection<AuthenticationHandler> jdbcAuthenticationHandlers,
            @Qualifier("emailPrincipalResolver") final PrincipalResolver emailPrincipalResolver,
            @Qualifier("mobilePrincipalResolver") final PrincipalResolver mobilePrincipalResolver) {
        return plan -> jdbcAuthenticationHandlers.forEach(h -> {
            switch (h.getName()) {
                case EMAIL_AUTHENTICATION_HANDLER:
                    plan.registerAuthenticationHandlerWithPrincipalResolver(h, emailPrincipalResolver);
                    break;
                case MOBILE_AUTHENTICATION_HANDLER:
                    plan.registerAuthenticationHandlerWithPrincipalResolver(h, mobilePrincipalResolver);
                    break;
                default:
                    plan.registerAuthenticationHandlerWithPrincipalResolver(h, new EchoingPrincipalResolver());
            }
        });
    }
}
