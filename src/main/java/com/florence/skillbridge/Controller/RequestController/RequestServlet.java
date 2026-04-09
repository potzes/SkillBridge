package com.florence.skillbridge.Controller.RequestController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.Response.ReceivedRequestDTO;
import com.florence.skillbridge.DTO.Response.SentRequestDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Exception.UnauthorizedException;
import com.florence.skillbridge.Service.Interface.UserService;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.BadRequestException;

import java.io.IOException;
import java.util.List;

@WebServlet("/requests/*")
public class RequestServlet extends HttpServlet {
    @Inject
    UserService userService;
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
            UserDTO userDTO = (UserDTO) request.getSession(false).getAttribute("userId");
            if (pathInfo == null || pathInfo.isBlank()) {
                String type;
                if(request.getParameter("type") != null) type = request.getParameter("type");
                else throw new BadRequestException("Invalid query");

                // GET /requests/type?received
                if (type.equals("received")) {
                    List<ReceivedRequestDTO> receivedRequestDTOS = userService.getReceivedRequests(userDTO);
                    jsonb.toJson(receivedRequestDTOS, response.getWriter());

                // GET /requests/type?sent
                } else if (type.equals("sent")) {
                    List<SentRequestDTO> sentRequestDTOS = userService.getSentRequests(userDTO);
                    jsonb.toJson(sentRequestDTOS, response.getWriter());
                } else {
                    throw new BadRequestException("Invalid query");
                }
            }
            else throw new BadRequestException("Invalid URL");
        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            String pathInfo = request.getPathInfo();
            if(pathInfo != null) {
                String[] parts = pathInfo.split("/");

                // PUT /requests/{request id}?response=(accept&chosen={skill id} / reject}
                if(parts.length == 2) {
                    int requestId = Integer.parseInt(parts[1]);
                    String responseParam = request.getParameter("response");

                    if(responseParam.equals("accept") && request.getParameter("chosen") != null) {
                        int chosenSkill = Integer.parseInt(request.getParameter("chosen"));
                        sessionCheck(request);
                        HttpSession session = request.getSession(false);
                        UserDTO userDTO = (UserDTO) session.getAttribute("userId");

                        userService.acceptRequest(userDTO, requestId, chosenSkill);
                        response.setStatus(201);
                    } else if (responseParam.equals("reject")) {
                        sessionCheck(request);
                        HttpSession session = request.getSession(false);
                        UserDTO userDTO = (UserDTO) session.getAttribute("userId");

                        userService.rejectRequest(userDTO, requestId);
                    }
                    else throw new BadRequestException("Invalid URL");
                }
                else {
                    throw new BadRequestException("Invalid URL");
                }
            } else  {
                throw new BadRequestException("Method not allowed");
            }

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            String pathInfo = request.getPathInfo();
            if(pathInfo == null) throw new BadRequestException("Path required");
            String[] parts = pathInfo.split("/");

            // DELETE /requests/{request id}
            if(parts.length == 2) {
                int requestId = Integer.parseInt(parts[1]);
                sessionCheck(request);
                HttpSession session = request.getSession(false);
                UserDTO userDTO = (UserDTO) session.getAttribute("userId");

                userService.cancelRequest(userDTO, requestId);
            }
            else {
                throw new BadRequestException("Invalid URL");
            }

        } catch (BadRequestException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "URL error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (ResourceNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO(404, "Resource error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "Parameter value error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (UnauthorizedException e) {
            ErrorDTO errorDTO = new ErrorDTO(401, "Authorization error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void sessionCheck(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("userId") == null) throw new UnauthorizedException("No existing session");
    }
}
