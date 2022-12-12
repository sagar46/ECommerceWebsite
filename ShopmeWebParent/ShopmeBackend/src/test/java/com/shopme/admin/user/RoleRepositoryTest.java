package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;
    @Test
    public void testCreateFirstRole(){
        Role roleAdmin = new Role("Admin","manage everything");
        Role savedRole = repository.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles(){
        Role roleSalesPerson = new Role("Sales Person","manage product price,customers,shipping,orders and sales report");
        Role roleEditor = new Role("Editor","manage categories,brands,products,articles and menus");
        Role roleShipper = new Role("Shipper","view orders,view products");
        Role roleAssistant = new Role("Assistant","manage questions and reviews");
        repository.saveAll(List.of(roleSalesPerson,roleEditor,roleShipper,roleAssistant));
    }
}
