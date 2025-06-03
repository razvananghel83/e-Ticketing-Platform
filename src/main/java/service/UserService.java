package service;

import model.Organiser;
import model.Participant;
import model.User;
import persistence.OrganiserRepository;
import persistence.ParticipantRepository;
import persistence.UserRepository;
import exceptions.AuthenticationException;
import exceptions.DuplicateUserException;

import java.util.ArrayList;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final OrganiserRepository organiserRepository = OrganiserRepository.getInstance();
    private final ParticipantRepository participantRepository = ParticipantRepository.getInstance();
    private User currentUser;

    public UserService() {
    }

    /**
     * Registers a new user and inserts into the correct table based on user type
     */
    public User register(String username, String email, String password, String userType) throws DuplicateUserException {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUserException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("Email already registered");
        }

        User newUser = new User(username, email, password, userType);
        newUser = userRepository.save(newUser);

        if ("ORGANISER".equalsIgnoreCase(userType)) {

            Organiser organiser = new Organiser(newUser.getUsername(), newUser.getEmail(),
                newUser.getPassword(), newUser.getUserType(), new ArrayList<>());
            organiser.setUserId(newUser.getUserId());
            organiserRepository.save(organiser);

        } else if ("PARTICIPANT".equalsIgnoreCase(userType)) {

            Participant participant = new Participant(newUser.getUsername(), newUser.getEmail(),
                newUser.getPassword(), newUser.getUserType(), new ArrayList<>());
            participant.setUserId(newUser.getUserId());
            participantRepository.save(participant);

        } else {
            throw new IllegalArgumentException("Invalid user type, must be either ORGANISER or PARTICIPANT");
        }

        return newUser;
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
     * If empty strings are provided for username or email, the current values are kept
     */
    public User updateUser(String newUsername, String newEmail, String newPassword)
            throws DuplicateUserException {

        if (currentUser == null) {
            throw new IllegalStateException("You are not logged in");
        }

        if (newUsername.isEmpty()) {
            newUsername = currentUser.getUsername();
        } else if (!newUsername.equals(currentUser.getUsername())) {

            Optional<User> existingWithUsername = userRepository.findByUsername(newUsername);
            if (existingWithUsername.isPresent()) {
                throw new DuplicateUserException("Username already taken");
            }
        }

        if (newEmail.isEmpty()) {
            newEmail = currentUser.getEmail();

        } else if (!newEmail.equals(currentUser.getEmail())) {

            Optional<User> existingWithEmail = userRepository.findByEmail(newEmail);
            if (existingWithEmail.isPresent()) {
                throw new DuplicateUserException("Email already registered");
            }
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