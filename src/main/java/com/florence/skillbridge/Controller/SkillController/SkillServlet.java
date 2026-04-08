package com.florence.skillbridge.Controller.SkillController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.Response.SkillDTO;
import com.florence.skillbridge.Exception.UnauthorizedException;
import com.florence.skillbridge.Service.Interface.SkillService;
import com.florence.skillbridge.Util.ErrorSender;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/skills")
public class SkillServlet extends HttpServlet {
    @Inject
    private SkillService skillService;
    @Inject
    ErrorSender errorSender;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new UnauthorizedException("No existing session");
            }

            List<SkillDTO> skillDTOS = skillService.getSkills();
            jsonb.toJson(skillDTOS, response.getWriter());
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(500);
            throw new RuntimeException(e.getMessage());
        }

    }
}
