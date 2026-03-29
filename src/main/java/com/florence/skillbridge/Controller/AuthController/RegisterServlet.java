package com.florence.skillbridge.Controller.AuthController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Service.Interface.AuthService;
import com.florence.skillbridge.DTO.Request.RegisterDTO;
import com.florence.skillbridge.Util.ErrorSender;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Inject
    AuthService authService;
    @Inject
    private ErrorSender errorSender;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            RegisterDTO registerDTO = jsonb.fromJson(request.getReader(), RegisterDTO.class);

            authService.register(registerDTO);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (ConflictException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "Field error", "Invalid user input", e.getErrors());
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
