package com.liyen.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.liyen.authentication.models.User;
import com.liyen.authentication.services.UserService;

@Controller
public class Users {
	private final UserService userService;
	
	public Users(UserService userService) {
		this.userService = userService;
	}
	
	// Form to register
	@RequestMapping("/registration")
	public String registerForm(@ModelAttribute("user") User user) {
		return "registrationPage.jsp";
	}
	
	// Form to login
	@RequestMapping("/login")
	public String login() {
		return "loginPage.jsp";
	}
	
	// process user registration
	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        // if result has errors, return the registration page (don't worry about validations just now)
		if (result.hasErrors()) {
			return "registrationPage.jsp";
		} else {
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
			User newUser = userService.registerUser(user);
			session.setAttribute("user_id", newUser.getId());
			return "redirect:/home";
		}
	}
	
	// process user login
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // if the user is authenticated, save their user id in session
		if (userService.authenticateUser(email, password)) {
			User user = userService.findByEmail(email);
			session.setAttribute("user_id", user.getId());
			return "redirect:/home";
		} else {  // else, add error messages and return the login page
			redirectAttributes.addFlashAttribute("login_error", "Invalid credentials. Please try again.");
			return "redirect:/login";
		}
       
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long user_id = (Long) session.getAttribute("user_id");
    	User user = userService.findUserById(user_id);
    	model.addAttribute("user", user);
    	return "homePage.jsp";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
        // redirect to login page
    	return "redirect:/login";
	}
}
