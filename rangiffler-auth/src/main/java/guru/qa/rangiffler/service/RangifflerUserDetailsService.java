package guru.qa.rangiffler.service;

import guru.qa.rangiffler.domain.RangifflerUserPrincipal;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class RangifflerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public RangifflerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new RangifflerUserPrincipal(user);
    }
}
