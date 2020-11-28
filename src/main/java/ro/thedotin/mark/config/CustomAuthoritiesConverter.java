package ro.thedotin.mark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserRepository userRepository;

    @Autowired
    public CustomAuthoritiesConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final String email = jwt.getClaimAsString("email");
        return List.of(this.userRepository.findByEmail(email)
                .map(User::getOrderPrivilege)
                .map(privilege -> (GrantedAuthority) () -> "ROLE_"+ privilege)
                .orElse((GrantedAuthority) ()->"NONE"));
    }
}
