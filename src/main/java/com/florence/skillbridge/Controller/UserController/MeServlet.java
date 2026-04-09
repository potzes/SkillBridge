package com.florence.skillbridge.Controller.UserController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import com.florence.skillbridge.DTO.Request.AddUserInterestDTO;
import com.florence.skillbridge.DTO.Request.AddUserSkillDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Exception.UnauthorizedException;
import com.florence.skillbridge.Service.Interface.UserService;
import com.florence.skillbridge.DTO.Response.MeDTO;
import com.florence.skillbridge.Util.ErrorSender;
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

@WebServlet({"/me/*"})
public class MeServlet extends HttpServlet {
    @Inject
    UserService userService;
    @Inject
    ErrorSender errorSender;
    @Inject
    private JsonbProvider jsonbProvider;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();

        try {
            String pathInfo = request.getPathInfo();

            // GET /me
            if (pathInfo == null || pathInfo.equals("/")) getMeHandler(request, response, jsonb);
            else throw new BadRequestException("Invalid path");

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Path error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null) {
                String[] parts = pathInfo.split("/");

                // POST /me/skills
                if (parts.length == 2 && parts[1].equals("skills")) {
                    postMeSkillHandler(request, response,jsonb);
                }
                // POST /me/interests
                else
                if (parts.length == 2 && parts[1].equals("interests")){
                    postMeInterestHandler(request, response, jsonb);
                } else {
                    throw new BadRequestException("Invalid path");
                }
            } else {
                throw new BadRequestException("Invalid Http Method");
            }
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON Error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "Invalid field(s)", e.getErrors());
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (ConflictException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(409, "Conflict error", Map.of("Conflict error", e.getMessage()));
            errorSender.sendError(userErrorDTO, response, jsonb);

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        String pathInfo = request.getPathInfo();
        String[] parts;
        try {
            if (pathInfo != null) {
                parts = pathInfo.split("/");

                // DELETE /me/skills/{skill id}
                if (parts.length == 3 && parts[1].equals("skills")) {
                    sessionCheck(request);
                    int skillId = Integer.parseInt(parts[2]);
                    UserDTO userDTO = (UserDTO) request.getSession().getAttribute("userId");
                    userService.deleteSkillToUser(skillId, userDTO);
                }
                // DELETE /me/interests/{skill id}
                else
                if (parts.length == 3 && parts[1].equals("interests")){
                    sessionCheck(request);
                    int skillId = Integer.parseInt(parts[2]);
                    UserDTO userDTO = (UserDTO) request.getSession().getAttribute("userId");
                    userService.deleteInterestToUser(skillId, userDTO);
                } else {
                    throw  new BadRequestException("Invalid path");
                }
            }
            else {
                throw new BadRequestException("Invalid Http Method");
            }
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Request error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

//-------------------Handler Methods--------------------------------------------------

    private void getMeHandler(HttpServletRequest request, HttpServletResponse response, Jsonb jsonb) throws IOException {

        HttpSession session = sessionCheck(request);

        UserDTO userDTO = (UserDTO) session.getAttribute("userId");
        MeDTO meDTO = userService.getMyInfo(userDTO);
        jsonb.toJson(meDTO, response.getWriter());
    }

    private void postMeSkillHandler(HttpServletRequest request, HttpServletResponse response, Jsonb jsonb) throws IOException {

        HttpSession session = sessionCheck(request);

        UserDTO userDTO = (UserDTO) session.getAttribute("userId");
        AddUserSkillDTO addSkill = jsonb.fromJson(request.getReader(), AddUserSkillDTO.class);

        userService.addSkillToUser(userDTO, addSkill);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    private void postMeInterestHandler(HttpServletRequest request, HttpServletResponse response, Jsonb jsonb) throws IOException {

        HttpSession session = sessionCheck(request);

        UserDTO userDTO = (UserDTO) session.getAttribute("userId");
        AddUserInterestDTO addInterest = jsonb.fromJson(request.getReader(), AddUserInterestDTO.class);

        userService.addInterestToUser(userDTO, addInterest);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }


//-----------------------Helper Methods----------------------------------------------

    private static HttpSession sessionCheck(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null)
            throw new UnauthorizedException("No existing session");
        return session;
    }
}
