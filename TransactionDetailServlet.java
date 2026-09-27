package com.gym.gymmembershipsystem.controller;

import com.gym.gymmembershipsystem.dao.TransactionDAO;
import com.gym.gymmembershipsystem.dao.TransactionDAOImpl;
import com.gym.gymmembershipsystem.model.TransactionDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionDetailServlet extends HttpServlet {

    private TransactionDAO transactionDAO;


    @Override
    public void init() {

        transactionDAO =
                new TransactionDAOImpl();
    }


    // =========================================================
    // DISPLAY TRANSACTION DETAILS / RECEIPT
    // =========================================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String transactionIdValue =
                request.getParameter("id");


        /*
         * Transaction ID is required.
         */
        if (transactionIdValue == null
                || transactionIdValue.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/transactions"
            );

            return;
        }


        try {

            int transactionId =
                    Integer.parseInt(
                            transactionIdValue
                    );


            TransactionDetail transactionDetail =
                    transactionDAO
                            .getTransactionDetailById(
                                    transactionId
                            );


            /*
             * Transaction not found.
             */
            if (transactionDetail == null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?notFound=1"
                );

                return;
            }


            /*
             * Send transaction details
             * to transaction-details.jsp.
             */
            request.setAttribute(
                    "transaction",
                    transactionDetail
            );


            request
                    .getRequestDispatcher(
                            "/transaction-details.jsp"
                    )
                    .forward(
                            request,
                            response
                    );


        } catch (NumberFormatException e) {

            /*
             * Invalid ID such as:
             *
             * /transaction-details?id=ABC
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/transactions?notFound=1"
            );


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load transaction details.",
                    e
            );
        }
    }
}