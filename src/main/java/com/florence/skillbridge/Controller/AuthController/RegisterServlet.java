package com.florence.skillbridge.Controller.AuthController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Service.Interface.AuthService;
import com.florence.skillbridge.DTO.Request.RegisterDTO;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;

import java.io.IOException;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Inject
    AuthService authService;
    @Inject
    private ErrorSender errorSender;
    @Inject
    private JsonbProvider jsonbProvider;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            RegisterDTO registerDTO = jsonb.fromJson(request.getReader(), RegisterDTO.class);

            authService.register(registerDTO);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (ConflictException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(409, "Conflict error", Map.of("Authorization error", e.getMessage()));
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "Field error", e.getErrors());
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
