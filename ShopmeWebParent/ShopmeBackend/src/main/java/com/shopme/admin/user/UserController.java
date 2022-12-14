package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtils;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    public String listFirstPage(Model model){
       return listByPage(1,model,"firstName","asc");
    }
    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum")int pageNum, Model model,
                             @Param("sortField") String sortField,@Param("sortDir") String sortDir){
        Page<User> page = userService.listByPage(pageNum,sortField,sortDir);
        System.out.println("Sort Field " + sortField);
        System.out.println("Sort Order " + sortDir);
        List<User> userList = page.getContent();
        long startCount =  (pageNum-1) * UserService.USERS_PER_PAGE+1;
        long endCount = startCount + UserService.USERS_PER_PAGE-1;
        if(endCount >  page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("userList",userList);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",reverseSortDir);
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
        if(!multpartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multpartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/"+savedUser.getId();
            FileUploadUtils.cleanDir(uploadDir);
            FileUploadUtils.saveFile(uploadDir,fileName,multpartFile);
        }else{
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message","The user has been saved successfully");
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
