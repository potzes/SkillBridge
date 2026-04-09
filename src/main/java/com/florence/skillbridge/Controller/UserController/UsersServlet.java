package com.florence.skillbridge.Controller.UserController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.Request.SendRequestDTO;
import com.florence.skillbridge.DTO.Request.UserFilterDTO;
import com.florence.skillbridge.DTO.Response.OtherProfileDTO;
import com.florence.skillbridge.DTO.Response.PagedUsersDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Exception.UnauthorizedException;
import com.florence.skillbridge.Service.Interface.RequestService;
import com.florence.skillbridge.Service.Interface.UserBrowseService;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
    @Inject
    UserBrowseService userBrowseService;
    @Inject
    RequestService requestService;
    @Inject
    private ErrorSender errorSender;
    @Inject
    private JsonbProvider jsonbProvider;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            sessionCheck(request);
            String pathInfo = request.getPathInfo();

            // /users?queries
            if(pathInfo == null) {
                String page = request.getParameter("page");
                String limit = request.getParameter("limit");
                String who = request.getParameter("who");
                String category = request.getParameter("category");
                String expertise = request.getParameter("expertise");
                String rating = request.getParameter("rating");
                UserDTO userDTO = (UserDTO) request.getSession(false).getAttribute("userId");

                UserFilterDTO userFilterDTO = new UserFilterDTO(page, limit, category, expertise, rating, who);
                PagedUsersDTO pagedUsersDTO = userBrowseService.getFilteredUsers(userFilterDTO, userDTO);

                jsonb.toJson(pagedUsersDTO, response.getWriter());
            }

            else {
                String[] parts = pathInfo.split("/");

                // /users/{user id}
                if(parts.length == 2) {
                    int userId = Integer.parseInt(parts[1]);

                    OtherProfileDTO otherProfileDTO = userBrowseService.getOtherProfile(userId);
                    jsonb.toJson(otherProfileDTO, response.getWriter());
                }
                else {
                    ErrorDTO errorDTO = new ErrorDTO(400, "URL error", "Invalid path");
                    errorSender.sendError(errorDTO, response, jsonb);
                }
            }
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(500);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();
        try {
            sessionCheck(request);
            String pathInfo = request.getPathInfo();
            String[] parts = pathInfo.split("/");

            // POST users/{user id}/requests
            if (parts.length == 3 && parts[2].equals("requests")) {
                UserDTO userDTO = (UserDTO) request.getSession(false).getAttribute("userId");
                SendRequestDTO sendRequestDTO = jsonb.fromJson(request.getReader(), SendRequestDTO.class);
                int senderId = userDTO.getId();
                int receiverId = Integer.parseInt(parts[1]);

                requestService.sendRequest(senderId, receiverId, sendRequestDTO);
                response.setStatus(201);
            } else {
                ErrorDTO errorDTO = new ErrorDTO(400, "URL error", "Invalid path");
                errorSender.sendError(errorDTO, response, jsonb);
            }
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ConflictException e) {
            ErrorDTO errorDTO = new ErrorDTO(409, "Conflict error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            response.sendError(500);
            throw new RuntimeException(e);
        }
    }

    private static void sessionCheck(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("userId") == null) throw new UnauthorizedException("No existing session");
    }
}
