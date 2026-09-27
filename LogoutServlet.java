package com.gym.gymmembershipsystem.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);


        /*
         * Remove login session.
         */
        if (session != null) {

            session.invalidate();
        }


        /*
         * Return to Login page.
         */
        response.sendRedirect(
                request.getContextPath()
                + "/login?logout=1"
        );
    }
}