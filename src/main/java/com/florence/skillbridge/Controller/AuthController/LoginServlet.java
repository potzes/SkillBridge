package com.florence.skillbridge.Controller.AuthController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Exception.InvalidCredentialsException;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Service.Interface.AuthService;
import com.florence.skillbridge.DTO.Request.LoginDTO;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Inject
    private AuthService authService;
    @Inject
    private ErrorSender errorSender;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            if(request.getSession(false) != null && request.getSession().getAttribute("userId") != null)
                throw new ConflictException("Session already exists");

            //POST /login
            LoginDTO loginDTO = jsonb.fromJson(request.getReader(), LoginDTO.class);

            UserDTO userDTO = authService.login(loginDTO);

            HttpSession session = request.getSession();
            session.setAttribute("userId", userDTO);
            session.setMaxInactiveInterval(60 * 60);

            response.setStatus(HttpServletResponse.SC_ACCEPTED);

        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON Error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "Invalid field(s)", "Invalid field(s)", e.getErrors());
            errorSender.sendError(userErrorDTO, response, jsonb);
        } catch (InvalidCredentialsException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(401, "Authentication Error", e.getMessage());
            errorSender.sendError(userErrorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
