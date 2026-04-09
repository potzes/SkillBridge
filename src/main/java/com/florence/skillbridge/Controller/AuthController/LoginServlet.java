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
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.BadRequestException;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Inject
    private AuthService authService;
    @Inject
    private ErrorSender errorSender;
    @Inject
    private JsonbProvider jsonbProvider;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            if(request.getSession(false) != null && request.getSession().getAttribute("userId") != null)
                throw new ConflictException("Session already exists");

            //POST /login
            LoginDTO loginDTO = jsonb.fromJson(request.getReader(), LoginDTO.class);

            UserDTO userDTO = authService.login(loginDTO);

            HttpSession session = request.getSession();
            session.setAttribute("userId", userDTO);
            session.setMaxInactiveInterval(60 * 60);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Set-Cookie",
                    "JSESSIONID=" + session.getId() +
                            "; Path=/SkillBridge; HttpOnly; SameSite=None ; Secure"
            );

        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON Error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (ConflictException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(409, "Conflict error", Map.of("Authorization error", e.getMessage()));
            errorSender.sendError(userErrorDTO, response, jsonb);

        }  catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "Invalid field(s)", e.getErrors());
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (InvalidCredentialsException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(401, "Authentication Error", Map.of("credential", e.getMessage()));
            errorSender.sendError(userErrorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
