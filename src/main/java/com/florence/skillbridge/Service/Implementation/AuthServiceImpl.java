package com.florence.skillbridge.Service.Implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.florence.skillbridge.DTO.Request.LoginDTO;
import com.florence.skillbridge.DTO.Request.RegisterDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.InvalidCredentialsException;
import com.florence.skillbridge.Repository.Interface.AuthRepo;
import com.florence.skillbridge.Service.Interface.AuthService;
import com.florence.skillbridge.Util.DTOValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    private AuthRepo authRepo;

    @Inject
    private DTOValidator validator;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        if(registerDTO.getEmail() == null || registerDTO.getUsername() == null || registerDTO.getPassword() == null) throw new BadRequestException("Missing JSON required field");

        validator.validate(registerDTO);
        isEmailOrUsernameExist(registerDTO.getEmail(), registerDTO.getUsername());

        User user = new User(registerDTO.getEmail(), registerDTO.getUsername(), hashPassword(registerDTO.getPassword()));
        authRepo.save(user);
    }

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        if(loginDTO.getUsernameOrEmail() == null || loginDTO.getPassword() == null) throw new BadRequestException("Missing JSON required field");
        validator.validate(loginDTO);

        String usernameOrEmail = loginDTO.getUsernameOrEmail();
        String password = loginDTO.getPassword();

        User user;
        if(isEmail(usernameOrEmail)) user = authRepo.findByEmail(usernameOrEmail).orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));
        else user = authRepo.findByUsername(usernameOrEmail).orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if(!verifiedPassword(password, user.getHashedPassword())) throw new InvalidCredentialsException("Invalid Credentials");

        return new UserDTO(user.getId());

    }

//---------------------Helper Methods-----------------------------------------------------

    private boolean verifiedPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    private boolean isEmail(String usernameOrEmail) {
        return usernameOrEmail.contains("@");
    }

    private void isEmailOrUsernameExist(String email, String username) {
        if(authRepo.findByEmail(email).isPresent()) throw new ConflictException("Email is already registered");
        if(authRepo.findByUsername(username).isPresent()) throw new ConflictException("Username already taken");
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

}
