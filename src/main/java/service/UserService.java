package service;

import model.User;
import persistance.UserRepository;
import exceptions.AuthenticationException;
import exceptions.DuplicateUserException;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private User currentUser; // Tracks logged-in user

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user
     */
    public User register(String username, String email, String password, String userType)
            throws DuplicateUserException {

        // Validate username/email uniqueness
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUserException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("Email already registered");
        }

        // Create and save user
        User newUser = new User(username, email, password, userType);
        return userRepository.save(newUser);
    }

    /**
     * Authenticates a user
     */
    public User login(String usernameOrEmail, String password)
            throws AuthenticationException {

        Optional<User> user = usernameOrEmail.contains("@")
                ? userRepository.findByEmail(usernameOrEmail)
                : userRepository.findByUsername(usernameOrEmail);

        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            throw new AuthenticationException("Invalid credentials");
        }

        this.currentUser = user.get();
        return currentUser;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Updates user details
     */
    public User updateUser(String newUsername, String newEmail, String newPassword)
            throws DuplicateUserException {

        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in");
        }

        // Validate new username/email uniqueness
        if (!newUsername.equals(currentUser.getUsername())){
            userRepository.findByUsername(newUsername).ifPresent(u -> {
                try {
                    throw new DuplicateUserException("Username already taken");
                } catch (DuplicateUserException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if (!newEmail.equals(currentUser.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(u -> {
                try {
                    throw new DuplicateUserException("Email already registered");
                } catch (DuplicateUserException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);

        userRepository.update(currentUser);
        return currentUser;
    }

    /**
     * Gets the currently logged-in user
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
}