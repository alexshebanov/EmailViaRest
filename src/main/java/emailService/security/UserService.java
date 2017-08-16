package emailService.security;


import emailService.dao.UserRepository;
import emailService.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s);
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), userAuthorities(user));
    }

    private List<GrantedAuthority> userAuthorities(User user){
        List<GrantedAuthority> result = new LinkedList<>();
        result.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.getLogin().equals("admin"))
            result.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return result;
    }
}
