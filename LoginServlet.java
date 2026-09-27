package com.gym.gymmembershipsystem.controller;

import com.gym.gymmembershipsystem.dao.UserDAO;
import com.gym.gymmembershipsystem.dao.UserDAOImpl;
import com.gym.gymmembershipsystem.model.User;
import com.gym.gymmembershipsystem.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;


    @Override
    public void init() {

        userDAO = new UserDAOImpl();
    }


    // =========================================================
    // DISPLAY LOGIN PAGE
    // =========================================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * If already logged in,
         * go directly to dashboard.
         */
        HttpSession session =
                request.getSession(false);

        if (session != null
                && session.getAttribute("loggedInUser") != null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/dashboard"
            );

            return;
        }


        request
                .getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }


    // =========================================================
    // PROCESS LOGIN
    // =========================================================
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");


        /*
         * Basic validation.
         */
        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.isEmpty()) {

            request.setAttribute(
                    "errorMessage",
                    "Please enter username and password."
            );

            request.setAttribute(
                    "username",
                    username
            );

            request
                    .getRequestDispatcher("/login.jsp")
                    .forward(request, response);

            return;
        }


        try {

            User user =
                    userDAO.findByUsername(
                            username.trim()
                    );


            /*
             * Username not found.
             */
            if (user == null) {

                loginFailed(
                        request,
                        response,
                        username
                );

                return;
            }


            /*
             * Check password against
             * the stored PBKDF2 hash.
             */
            boolean passwordCorrect =
                    PasswordUtil.verifyPassword(
                            password,
                            user.getPasswordHash()
                    );


            if (!passwordCorrect) {

                loginFailed(
                        request,
                        response,
                        username
                );

                return;
            }


            /*
             * LOGIN SUCCESS
             */

            HttpSession oldSession =
                    request.getSession(false);


            /*
             * Remove old session before
             * creating a new login session.
             */
            if (oldSession != null) {

                oldSession.invalidate();
            }


            HttpSession session =
                    request.getSession(true);


            /*
             * Store required user information
             * in the session.
             */
            session.setAttribute(
                    "loggedInUser",
                    user
            );


            session.setAttribute(
                    "userId",
                    user.getUserId()
            );


            session.setAttribute(
                    "username",
                    user.getUsername()
            );


            session.setAttribute(
                    "fullName",
                    user.getFullName()
            );


            session.setAttribute(
                    "role",
                    user.getRole()
            );


            /*
             * Session expires after
             * 30 minutes of inactivity.
             */
            session.setMaxInactiveInterval(
                    30 * 60
            );


            /*
             * Login success:
             * redirect to Dashboard.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/dashboard"
            );


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to process login.",
                    e
            );
        }
    }


    // =========================================================
    // LOGIN FAILED
    // =========================================================
    private void loginFailed(
            HttpServletRequest request,
            HttpServletResponse response,
            String username)
            throws ServletException, IOException {

        /*
         * Don't tell the user whether
         * username OR password was wrong.
         */
        request.setAttribute(
                "errorMessage",
                "Invalid username or password."
        );


        request.setAttribute(
                "username",
                username
        );


        request
                .getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }
}