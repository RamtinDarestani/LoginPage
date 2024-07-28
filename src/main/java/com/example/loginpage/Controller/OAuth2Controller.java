package com.example.loginpage.Controller;

import com.example.loginpage.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class OAuth2Controller {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String handleOAuth2LoginSuccess(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String picture = oauth2User.getAttribute("picture");

        logger.info("Handling OAuth2 login success - Email: {}, First Name: {}, Last Name: {}", email, firstName, lastName);

        try {
            User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(new User());

            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setProfilePicture(picture);

            entityManager.persist(user);
            logger.info("User saved successfully with ID: {}", user.getId());

            return "redirect:/user-profile";
        } catch (Exception e) {
            logger.error("Error saving user: ", e);
            return "redirect:/error";
        }
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            User user = new User();
            user.setEmail(oauth2User.getAttribute("email"));
            user.setFirstName(oauth2User.getAttribute("given_name"));
            user.setLastName(oauth2User.getAttribute("family_name"));
            user.setProfilePicture(oauth2User.getAttribute("picture"));
            model.addAttribute("user", user);
            logger.info("User profile loaded for email: {}", user.getEmail());
        } else {
            logger.warn("No authenticated user found when accessing user profile");
        }
        return "user-profile";
    }

    @GetMapping("/error")
    public String error() {
        logger.error("Error page accessed");
        return "error";
    }
}