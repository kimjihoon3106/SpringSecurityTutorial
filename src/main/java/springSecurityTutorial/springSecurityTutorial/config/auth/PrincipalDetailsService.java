package springSecurityTutorial.springSecurityTutorial.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import springSecurityTutorial.springSecurityTutorial.Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springSecurityTutorial.springSecurityTutorial.repository.UserRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username : "+ username);
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null) {
            return new PrincipalDetails(userEntity);
        } else {
            return null;
        }

    }
}
