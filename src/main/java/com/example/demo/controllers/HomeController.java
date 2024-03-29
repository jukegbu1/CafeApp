package com.example.demo.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.beans.Menu;
import com.example.demo.beans.User;
import com.example.demo.configurations.CloudinaryConfig;
import com.example.demo.repositories.MenuRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

  private UserService userService;

  @Autowired
  CloudinaryConfig cloudc;

  @Autowired
  MenuRepository menuRepository;

  @RequestMapping("/")
  public String index(Model model) {
    model.addAttribute("menus", menuRepository.findAll());
    return "index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public String showRegistrationPage(Model model) {
    model.addAttribute("user", new User());
    return "registration";
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String processRegistrationPage(@Valid @ModelAttribute("user")
                                                User user, BindingResult
                                                result,
                                        Model model) {
    model.addAttribute("user", new User());
    if (result.hasErrors()) {
      return "registration";
    } else {
      userService.saveUser(user);
      model.addAttribute("message", "User Account Successfully Created");
    }
    return "index";
  }

  @GetMapping("/add")
  public String getMenuForm(Model model) {
    model.addAttribute("menu", new Menu());
    return "menuform";
  }

  @PostMapping("/process")
  public String processForm(@Valid
                            @ModelAttribute Menu menu, BindingResult result, @RequestParam("file") MultipartFile file){
    if (file.isEmpty()) {
      return "redirect:/add";
    }
    if (result.hasErrors()) {
      return "menuform";
    }try {
      Map uploadResult = cloudc.upload(file.getBytes(),
              ObjectUtils.asMap("resourcetype", "auto"));
      menu.setImage(uploadResult.get("url").toString());
      menuRepository.save(menu);
    }catch (IOException e) {
      e.printStackTrace();
      return "redirect:/add";
    }
    menuRepository.save(menu);
    return "redirect:/";
  }
}