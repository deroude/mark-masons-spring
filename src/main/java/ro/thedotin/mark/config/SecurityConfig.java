package ro.thedotin.mark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;


@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Converter<Jwt, Collection<GrantedAuthority>> customConverter;

    @Autowired
    public SecurityConfig(Converter<Jwt, Collection<GrantedAuthority>> customConverter) {
        this.customConverter = customConverter;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and().authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .anyRequest().authenticated()
        )
                .oauth2ResourceServer(configurer -> configurer.jwt()
                        .jwtAuthenticationConverter(this.getJwtAuthenticationConverter()));
    }

    private JwtAuthenticationConverter getJwtAuthenticationConverter() {
        JwtAuthenticationConverter re = new JwtAuthenticationConverter();
        re.setJwtGrantedAuthoritiesConverter(customConverter);
        return re;
    }
}