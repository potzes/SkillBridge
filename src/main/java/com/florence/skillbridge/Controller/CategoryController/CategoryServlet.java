package com.florence.skillbridge.Controller.CategoryController;

import com.florence.skillbridge.DTO.ErrorResponse.ErrorDTO;
import com.florence.skillbridge.DTO.Response.CategoryDTO;
import com.florence.skillbridge.Service.Interface.CategoryService;
import com.florence.skillbridge.Util.ErrorSender;
import com.florence.skillbridge.Util.JsonbProvider;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    @Inject
    JsonbProvider jsonbProvider;
    @Inject
    CategoryService categoryService;
    @Inject
    ErrorSender errorSender;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jsonb jsonb = jsonbProvider.getJsonb();

        try {
            List<CategoryDTO> categoryDTOS = categoryService.getCategories();

            jsonb.toJson(categoryDTOS, response.getWriter());
        } catch (JsonbException e) {
            ErrorDTO errorDTO = new ErrorDTO(400, "JSON error", e.getMessage());
            errorSender.sendError(errorDTO, response, jsonb);
        } catch (Exception e) {
            response.setStatus(500);
            throw new RuntimeException(e);
        }
    }
}
