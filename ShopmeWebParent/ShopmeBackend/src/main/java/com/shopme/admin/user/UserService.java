package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> listAll(){
        return (List<User>) userRepository.findAll();
    }
    public List<Role> listRoles(){
        return (List<Role>) roleRepository.findAll();
    }
    public void save(User user){
        encodePassword(user);
        userRepository.save(user);
    }
    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public boolean isEmailUnique(String email){
        User user = userRepository.getUserByEmail(email);
        return user == null;
    }
}
