package com.gym.gymmembershipsystem.controller;

import com.gym.gymmembershipsystem.dao.TransactionDAO;
import com.gym.gymmembershipsystem.dao.TransactionDAOImpl;
import com.gym.gymmembershipsystem.model.TransactionRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TransactionServlet extends HttpServlet {

    private TransactionDAO transactionDAO;


    @Override
    public void init() {

        transactionDAO =
                new TransactionDAOImpl();
    }


    // =========================================================
    // DISPLAY / SEARCH TRANSACTIONS
    // =========================================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String searchTerm =
                    request.getParameter(
                            "searchTerm"
                    );

            String customerType =
                    request.getParameter(
                            "customerType"
                    );


            List<TransactionRecord> transactions;


            /*
             * No search/filter entered:
             * show every transaction.
             */
            if (
                    (searchTerm == null
                    || searchTerm.trim().isEmpty())
                    &&
                    (customerType == null
                    || customerType.trim().isEmpty())
            ) {

                transactions =
                        transactionDAO
                                .getAllTransactions();

            } else {

                transactions =
                        transactionDAO
                                .searchTransactions(
                                        searchTerm,
                                        customerType
                                );
            }


            request.setAttribute(
                    "transactions",
                    transactions
            );


            request.setAttribute(
                    "searchTerm",
                    searchTerm
            );


            request.setAttribute(
                    "customerType",
                    customerType
            );


            request
                    .getRequestDispatcher(
                            "/transactions.jsp"
                    )
                    .forward(
                            request,
                            response
                    );


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load transaction records.",
                    e
            );
        }
    }


    // =========================================================
    // DELETE TRANSACTION
    // =========================================================
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter(
                        "action"
                );


        if ("delete".equals(action)) {

            deleteTransaction(
                    request,
                    response
            );

            return;
        }


        response.sendRedirect(
                request.getContextPath()
                + "/transactions"
        );
    }


    // =========================================================
    // DELETE
    // =========================================================
    private void deleteTransaction(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String transactionIdValue =
                    request.getParameter(
                            "transactionId"
                    );


            if (
                    transactionIdValue == null
                    ||
                    transactionIdValue.trim().isEmpty()
            ) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?deleteError=1"
                );

                return;
            }


            int transactionId =
                    Integer.parseInt(
                            transactionIdValue
                    );


            boolean deleted =
                    transactionDAO
                            .deleteTransaction(
                                    transactionId
                            );


            if (deleted) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?deleted=1"
                );

            } else {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?deleteError=1"
                );
            }


        } catch (NumberFormatException e) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/transactions?deleteError=1"
            );


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to delete transaction.",
                    e
            );
        }
    }
}