package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
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
        boolean isUpdatingUser = (user.getId() != null);
        if(isUpdatingUser){
            User existingUser = userRepository.findById(user.getId()).get();
            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }else{
                encodePassword(user);
            }
        }else{
            encodePassword(user);
        }
        userRepository.save(user);
    }
    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public boolean isEmailUnique(Integer id,String email){
        User user = userRepository.getUserByEmail(email);
        if(user == null) return true;
        boolean isCreatingNew = (id == null);
        if(isCreatingNew){
            if(user != null) return false;
        }else {
            if(user.getId() != id) return  false;
        }
        return true;
    }
    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex){
            throw  new UserNotFoundException("Could not find any user with id " + id);
        }
    }
    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if(countById == null || countById ==0){
            throw new UserNotFoundException("Could not find any user with id "+ id);
        }
        userRepository.deleteById(id);
    }
    public void updateUserEnabledStatus(Integer id, boolean enabled){
        userRepository.updateEnableStatus(id,enabled);
    }
}
