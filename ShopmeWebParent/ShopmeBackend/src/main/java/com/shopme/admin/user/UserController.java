package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtils;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/users")
    public String listAll(Model model){
        List<User> userList = userService.listAll();
        model.addAttribute("userList",userList);
        return "users";
    }
    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRole = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRole);
        model.addAttribute("pageTitle","Create New User");
        return "users_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multpartFile) throws IOException {
        System.out.println(user);
        System.out.println(multpartFile.getOriginalFilename());
        String fileName = StringUtils.cleanPath(multpartFile.getOriginalFilename());
        String uploadDir = "user-photos";
        FileUploadUtils.saveFile(uploadDir,fileName,multpartFile);
//        userService.save(user);
//        redirectAttributes.addFlashAttribute("message","The user has been saved successfully");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,RedirectAttributes redirectAttributes,Model model) throws UserNotFoundException {
        try {
            User user = userService.get(id);
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("listRoles",listRoles);            model.addAttribute("user",user);
            model.addAttribute("pageTitle","Edit User (ID: "+ id+")");
            return "users_form";
        } catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message",ex.getMessage());
            return "redirect:/users";
        }
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,RedirectAttributes redirectAttributes, Model model){
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message","User id "+id+" has been deleted successfully");
        } catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message",ex.getMessage());
        }
        return "redirect:/users";
    }
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,RedirectAttributes redirectAttributes){
        userService.updateUserEnabledStatus(id,enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID "+ id + " has been " + status;
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/users";
     }
}
