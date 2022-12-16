package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Test
    public void testCreateNewUserWithOneRole(){
        Role roleAdmin = entityManager.find(Role.class,1);
        User userSagar = new User("sagar.kr1910@gmail.com","sagar","Sagar","Kumar");
        userSagar.addRole(roleAdmin);
        User savedUser = userRepository.save(userSagar);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testCreateNewUserWithTwoRoles(){
        User userRavi = new User("ravi@gmail.com","ravi","Ravi","Prakash");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userRavi.addRole(roleAssistant);
        userRavi.addRole(roleEditor);
        User savedUser = userRepository.save(userRavi);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testListAllUsers(){
        Iterable<User> listUser = userRepository.findAll();
        listUser.forEach(user -> {
            System.out.println(user);
        });
    }
    @Test
    public void testGetUserByID(){
        User user = userRepository.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user = userRepository.findById(2).get();
        user.setEnabled(true);
        user.setEmail("ravi0303@gmail.com");
    }

    @Test
    public void testUpdateUserRoles(){
        User user = userRepository.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesPerson = new Role(2);
        user.getRoles().remove(roleEditor);
        user.addRole(roleSalesPerson);
        userRepository.save(user);
    }
    @Test
    public  void testDeleteUserById(){
        Integer userId = 2;
        userRepository.deleteById(userId);
    }
    @Test
    public void testGetUserBYEMail(){
        String email = "ravi@gmail.com";
        User user = userRepository.getUserByEmail(email);
        assertThat(user).isNotNull();
    }
}
