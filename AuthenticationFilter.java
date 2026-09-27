package com.gym.gymmembershipsystem.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }


    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;


        String contextPath =
                httpRequest.getContextPath();

        String requestURI =
                httpRequest.getRequestURI();


        /*
         * Pages that do not require login.
         */
        boolean loginRequest =
                requestURI.equals(
                        contextPath + "/login"
                );

        boolean loginPage =
                requestURI.equals(
                        contextPath + "/login.jsp"
                );

        boolean indexPage =
                requestURI.equals(
                        contextPath + "/"
                )
                ||
                requestURI.equals(
                        contextPath + "/index.html"
                );


        /*
         * Allow Login and Index without session.
         */
        if (loginRequest
                || loginPage
                || indexPage) {

            chain.doFilter(
                    request,
                    response
            );

            return;
        }


        /*
         * Check login session.
         */
        HttpSession session =
                httpRequest.getSession(false);


        boolean loggedIn =
                session != null
                &&
                session.getAttribute(
                        "loggedInUser"
                ) != null;


        if (loggedIn) {

            /*
             * User is authenticated.
             */
            chain.doFilter(
                    request,
                    response
            );

        } else {

            /*
             * User is not authenticated.
             */
            httpResponse.sendRedirect(
                    contextPath + "/login"
            );
        }
    }


    @Override
    public void destroy() {
    }
}