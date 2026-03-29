package com.florence.skillbridge.Util;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@ApplicationScoped
public class ErrorSender {
    public void sendError(ErrorDTO errorDTO, HttpServletResponse response, Jsonb jsonb) throws IOException {
        response.setStatus(errorDTO.getStatus());
        response.setContentType("application/json");
        jsonb.toJson(errorDTO, response.getWriter());
    }

    public void sendError(UserErrorDTO userErrorDTO, HttpServletResponse response, Jsonb jsonb) throws IOException {
        response.setStatus(userErrorDTO.getStatus());
        response.setContentType("application/json");
        jsonb.toJson(userErrorDTO, response.getWriter());
    }
}
