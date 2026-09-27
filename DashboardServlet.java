package com.gym.gymmembershipsystem.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get existing session.
         *
         * false = do not create a new session
         * if one does not already exist.
         */
        HttpSession session =
                request.getSession(false);


        /*
         * User has not logged in.
         */
        if (session == null
                || session.getAttribute("loggedInUser") == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }


        /*
         * User is logged in.
         * Show Dashboard.
         */
        request
                .getRequestDispatcher(
                        "/dashboard.jsp"
                )
                .forward(
                        request,
                        response
                );
    }
}