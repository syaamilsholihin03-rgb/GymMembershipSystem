package com.gym.gymmembershipsystem.controller;

import com.gym.gymmembershipsystem.dao.GymDAO;
import com.gym.gymmembershipsystem.dao.GymDAOImpl;
import com.gym.gymmembershipsystem.dao.TransactionDAO;
import com.gym.gymmembershipsystem.dao.TransactionDAOImpl;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import com.gym.gymmembershipsystem.model.TransactionDetail;

import com.gym.gymmembershipsystem.service.PriceCalculator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditTransactionServlet extends HttpServlet {

    private TransactionDAO transactionDAO;
    private GymDAO gymDAO;
    private PriceCalculator priceCalculator;


    @Override
    public void init() {

        transactionDAO =
                new TransactionDAOImpl();

        gymDAO =
                new GymDAOImpl();

        priceCalculator =
                new PriceCalculator();
    }


    // =========================================================
    // DISPLAY EDIT PAGE
    // =========================================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String idValue =
                request.getParameter("id");


        if (idValue == null
                || idValue.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/transactions"
            );

            return;
        }


        try {

            int transactionId =
                    Integer.parseInt(
                            idValue
                    );


            TransactionDetail detail =
                    transactionDAO
                            .getTransactionDetailById(
                                    transactionId
                            );


            if (detail == null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?notFound=1"
                );

                return;
            }


            showEditPage(
                    request,
                    response,
                    detail,
                    null
            );


        } catch (NumberFormatException e) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/transactions?notFound=1"
            );


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load transaction.",
                    e
            );
        }
    }


    // =========================================================
    // UPDATE TRANSACTION
    // =========================================================
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        TransactionDetail detail = null;


        try {

            int transactionId =
                    Integer.parseInt(
                            request.getParameter(
                                    "transactionId"
                            )
                    );


            detail =
                    transactionDAO
                            .getTransactionDetailById(
                                    transactionId
                            );


            if (detail == null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?notFound=1"
                );

                return;
            }


            String customerName =
                    clean(
                            request.getParameter(
                                    "customerName"
                            )
                    );


            if (customerName.isEmpty()) {

                throw new IllegalArgumentException(
                        "Customer name is required."
                );
            }


            detail.setCustomerName(
                    customerName
            );


            // =================================================
            // MEMBERSHIP
            // =================================================
            if ("MEMBER".equals(
                    detail.getCustomerType())) {


                String phone =
                        clean(
                                request.getParameter(
                                        "phone"
                                )
                        );


                String email =
                        clean(
                                request.getParameter(
                                        "email"
                                )
                        );


                String gender =
                        clean(
                                request.getParameter(
                                        "gender"
                                )
                        );


                if (phone.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Phone number is required."
                    );
                }


                if (email.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Email is required."
                    );
                }


                if (gender.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Gender is required."
                    );
                }


                int packageId =
                        Integer.parseInt(
                                request.getParameter(
                                        "packageId"
                                )
                        );


                int numberOfMonths =
                        Integer.parseInt(
                                request.getParameter(
                                        "numberOfMonths"
                                )
                        );


                if (numberOfMonths < 1
                        || numberOfMonths > 24) {

                    throw new IllegalArgumentException(
                            "Number of months must be between 1 and 24."
                    );
                }


                GymPackage gymPackage =
                        getPackageById(
                                packageId
                        );


                if (gymPackage == null) {

                    throw new IllegalArgumentException(
                            "Invalid membership package."
                    );
                }


                BigDecimal total =
                        priceCalculator
                                .calculateMembership(
                                        gymPackage,
                                        numberOfMonths
                                );


                detail.setPhone(
                        phone
                );


                detail.setEmail(
                        email
                );


                detail.setGender(
                        gender
                );


                detail.setPackageId(
                        packageId
                );


                detail.setPackageName(
                        gymPackage.getPackageName()
                );


                detail.setNumberOfMonths(
                        numberOfMonths
                );


                detail.setPersonalTrainingIncluded(
                        gymPackage
                                .isPersonalTrainingIncluded()
                );


                detail.setWalkInFee(
                        BigDecimal.ZERO
                );


                detail.setTotalAmount(
                        total
                );


                detail.setServices(
                        new ArrayList<>()
                );
            }


            // =================================================
            // WALK-IN
            // =================================================
            else if ("WALK_IN".equals(
                    detail.getCustomerType())) {


                List<GymService> selectedServices =
                        getSelectedServices(
                                request
                        );


                BigDecimal total =
                        priceCalculator
                                .calculateWalkIn(
                                        selectedServices
                                );


                detail.setPackageId(
                        null
                );


                detail.setPackageName(
                        "-"
                );


                detail.setNumberOfMonths(
                        0
                );


                detail.setWalkInFee(
                        priceCalculator
                                .getWalkInFee()
                );


                detail.setServices(
                        selectedServices
                );


                detail.setTotalAmount(
                        total
                );
            }


            boolean updated =
                    transactionDAO
                            .updateTransaction(
                                    detail
                            );


            if (updated) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?updated=1"
                );

            } else {

                showEditPage(
                        request,
                        response,
                        detail,
                        "Unable to update transaction."
                );
            }


        } catch (IllegalArgumentException e) {

            if (detail != null) {

                try {

                    showEditPage(
                            request,
                            response,
                            detail,
                            e.getMessage()
                    );

                } catch (SQLException sqlException) {

                    throw new ServletException(
                            "Unable to reload edit page.",
                            sqlException
                    );
                }

            } else {

                response.sendRedirect(
                        request.getContextPath()
                        + "/transactions?updateError=1"
                );
            }


        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to update transaction.",
                    e
            );
        }
    }


    // =========================================================
    // DISPLAY EDIT PAGE
    // =========================================================
    private void showEditPage(
            HttpServletRequest request,
            HttpServletResponse response,
            TransactionDetail detail,
            String errorMessage)
            throws SQLException,
            ServletException,
            IOException {


        request.setAttribute(
                "transaction",
                detail
        );


        request.setAttribute(
                "packages",
                gymDAO.getAllPackages()
        );


        request.setAttribute(
                "services",
                gymDAO.getAllServices()
        );


        if (errorMessage != null) {

            request.setAttribute(
                    "errorMessage",
                    errorMessage
            );
        }


        request
                .getRequestDispatcher(
                        "/edit-transaction.jsp"
                )
                .forward(
                        request,
                        response
                );
    }


    // =========================================================
    // GET PACKAGE
    // =========================================================
    private GymPackage getPackageById(
            int packageId)
            throws SQLException {

        List<GymPackage> packages =
                gymDAO.getAllPackages();


        for (GymPackage gymPackage : packages) {

            if (gymPackage.getPackageId()
                    == packageId) {

                return gymPackage;
            }
        }


        return null;
    }


    // =========================================================
    // GET WALK-IN SERVICES
    // =========================================================
    private List<GymService> getSelectedServices(
            HttpServletRequest request)
            throws SQLException {

        List<GymService> selectedServices =
                new ArrayList<>();


        String[] selectedIds =
                request.getParameterValues(
                        "serviceId"
                );


        if (selectedIds == null) {

            return selectedServices;
        }


        List<GymService> allServices =
                gymDAO.getAllServices();


        for (String selectedId : selectedIds) {


            int serviceId =
                    Integer.parseInt(
                            selectedId
                    );


            for (GymService service : allServices) {


                if (service.getServiceId()
                        == serviceId) {


                    /*
                     * Walk-In only supports:
                     * Sauna and Locker Rental.
                     */
                    if ("Sauna".equalsIgnoreCase(
                            service.getServiceName())
                            ||
                            "Locker Rental".equalsIgnoreCase(
                                    service.getServiceName())) {


                        selectedServices.add(
                                service
                        );
                    }


                    break;
                }
            }
        }


        return selectedServices;
    }


    private String clean(
            String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}