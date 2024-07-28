package com.example.loginpage.Controller;

import com.example.loginpage.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        try {
            List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching users: ", e);
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @GetMapping("/test-db")
    public String testDatabase() {
        logger.info("Testing database connection");
        try {
            long count = (long) entityManager.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
            logger.info("Database connection successful. User count: {}", count);
            return "Connection successful. User count: " + count;
        } catch (Exception e) {
            logger.error("Database connection failed: ", e);
            return "Database connection failed: " + e.getMessage();
        }
    }
}