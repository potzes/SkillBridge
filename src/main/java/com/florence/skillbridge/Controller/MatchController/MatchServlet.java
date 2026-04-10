package com.florence.skillbridge.Controller.MatchController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.ErrorResponse.UserErrorDTO;
import com.florence.skillbridge.DTO.Request.FeedbackDTO;
import com.florence.skillbridge.DTO.Request.SendMessageDTO;
import com.florence.skillbridge.DTO.Response.MatchesDTO;
import com.florence.skillbridge.DTO.Response.PagedMessageDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Exception.UnauthorizedException;
import com.florence.skillbridge.Service.Interface.MatchService;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.BadRequestException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/matches/*")
public class MatchServlet extends HttpServlet {
    @Inject
    MatchService matchService;
    @Inject
    ErrorSender errorSender;
    @Inject
    private JsonbProvider jsonbProvider;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        HttpSession session = request.getSession(false);
        sessionCheck(session);
        try {
            if(request.getPathInfo() == null) {
                UserDTO userDTO = (UserDTO) session.getAttribute("userId");

                List<MatchesDTO> matchesDTOS = matchService.getMatches(userDTO);
                jsonb.toJson(matchesDTOS, response.getWriter());

                 // GET matches/{match id}/messages?page={}&limit={}
            } else {
                String pathInfo = request.getPathInfo();
                String[] parts = pathInfo.split("/");
                String page = request.getParameter("page");
                String limit = request.getParameter("limit");

                if(parts.length == 3 && parts[2].equals("messages")) {
                    UserDTO userDTO = (UserDTO) session.getAttribute("userId");
                    int matchId = Integer.parseInt(parts[1]);

                    PagedMessageDTO pagedMessageDTO = matchService.getPagedMessages(userDTO, matchId, page, limit);
                    jsonb.toJson(pagedMessageDTO, response.getWriter());
                }
            }
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Path error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            HttpSession session = request.getSession(false);
            sessionCheck(session);
            UserDTO userDTO = (UserDTO) session.getAttribute("userId");
            String pathInfo = request.getPathInfo();
            String endParam = request.getParameter("end");
            if(pathInfo == null) throw new BadRequestException("Path required");
            if(endParam == null) throw new BadRequestException("Parameter required");

            String[] parts = pathInfo.split("/");

            // PUT matches/{match id}?end=true
            if(parts.length == 2) {
                int matchId = Integer.parseInt(parts[1]);

                matchService.endMatch(userDTO, matchId, endParam);
            } else throw new BadRequestException("Invalid URL");

        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Path data type error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            HttpSession session = request.getSession(false);
            sessionCheck(session);
            String pathInfo = request.getPathInfo();

            if(pathInfo == null) throw new BadRequestException("Path required");

            String[] parts = pathInfo.split("/");

            // POST matches/{match id}/feedbacks
            if(parts.length == 3 && parts[2].equals("feedbacks")) {
                UserDTO userDTO = (UserDTO) session.getAttribute("userId");
                int matchId = Integer.parseInt(parts[1]);
                FeedbackDTO feedbackDTO = jsonb.fromJson(request.getReader(), FeedbackDTO.class);

                matchService.submitFeedback(userDTO, matchId, feedbackDTO);
                response.setStatus(201);

            // POST matches/{match id}/messages
            } else if(parts.length == 3 && parts[2].equals("messages")) {
                UserDTO userDTO = (UserDTO) session.getAttribute("userId");
                int matchId = Integer.parseInt(parts[1]);
                SendMessageDTO sendMessageDTO = jsonb.fromJson(request.getReader(), SendMessageDTO.class);
                matchService.sendMessage(userDTO, matchId, sendMessageDTO);
                response.setStatus(201);
            } else throw new BadRequestException("Invalid URL");

        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (DTOValidationException e) {
            UserErrorDTO userErrorDTO = new UserErrorDTO(400, "URL error", Map.of("Message error", e.getMessage()));
            errorSender.sendError(userErrorDTO, response, jsonb);
        } catch (ConflictException e) {
            ErrorDTO errorDTO = new ErrorDTO(409, "Conflict error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Path data type error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void sessionCheck(HttpSession session) {
        if(session == null || session.getAttribute("userId") == null) throw new UnauthorizedException("No existing session");
    }

}
