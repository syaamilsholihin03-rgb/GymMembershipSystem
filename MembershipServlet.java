package com.gym.gymmembershipsystem.controller;

import com.gym.gymmembershipsystem.dao.GymDAO;
import com.gym.gymmembershipsystem.dao.GymDAOImpl;
import com.gym.gymmembershipsystem.dao.TransactionDAO;
import com.gym.gymmembershipsystem.dao.TransactionDAOImpl;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import com.gym.gymmembershipsystem.model.GymTransaction;
import com.gym.gymmembershipsystem.model.Member;

import com.gym.gymmembershipsystem.service.PriceCalculator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembershipServlet extends HttpServlet {

    private GymDAO gymDAO;
    private TransactionDAO transactionDAO;
    private PriceCalculator priceCalculator;


    @Override
    public void init() {

        gymDAO =
                new GymDAOImpl();

        transactionDAO =
                new TransactionDAOImpl();

        priceCalculator =
                new PriceCalculator();
    }


    // =========================================================
    // LOAD REGISTRATION PAGE
    // =========================================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            loadFormData(request);

            request
                    .getRequestDispatcher(
                            "/membership.jsp"
                    )
                    .forward(
                            request,
                            response
                    );

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load registration page.",
                    e
            );
        }
    }


    // =========================================================
    // PROCESS FORM
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

        try {

            if ("calculate".equals(action)) {

                calculate(
                        request,
                        response
                );

            } else if ("save".equals(action)) {

                save(
                        request,
                        response
                );

            } else {

                response.sendRedirect(
                        request.getContextPath()
                        + "/membership"
                );
            }

        } catch (SQLException e) {

            throw new ServletException(
                    "Database error.",
                    e
            );

        } catch (NumberFormatException e) {

            throw new ServletException(
                    "Invalid number entered.",
                    e
            );

        } catch (IllegalArgumentException e) {

            throw new ServletException(
                    e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // CALCULATE
    // =========================================================
    private void calculate(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        String registrationType =
                request.getParameter(
                        "registrationType"
                );


        preserveCustomerInformation(
                request
        );


        if ("MEMBER".equals(
                registrationType)) {

            calculateMembership(
                    request
            );

        } else if ("WALK_IN".equals(
                registrationType)) {

            calculateWalkIn(
                    request
            );

        } else {

            throw new IllegalArgumentException(
                    "Please select a registration type."
            );
        }


        loadFormData(
                request
        );


        request
                .getRequestDispatcher(
                        "/membership.jsp"
                )
                .forward(
                        request,
                        response
                );
    }


    // =========================================================
    // CALCULATE MEMBERSHIP
    // =========================================================
    private void calculateMembership(
            HttpServletRequest request)
            throws SQLException {

        String packageValue =
                request.getParameter(
                        "packageId"
                );

        String monthsValue =
                request.getParameter(
                        "numberOfMonths"
                );


        if (packageValue == null
                || packageValue.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a membership package."
            );
        }


        if (monthsValue == null
                || monthsValue.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please enter number of months."
            );
        }


        int packageId =
                Integer.parseInt(
                        packageValue
                );


        int numberOfMonths =
                Integer.parseInt(
                        monthsValue
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


        BigDecimal total =
                priceCalculator
                        .calculateMembership(
                                gymPackage,
                                numberOfMonths
                        );


        request.setAttribute(
                "calculatedTotal",
                total
        );


        request.setAttribute(
                "selectedPackageId",
                packageId
        );


        request.setAttribute(
                "selectedMonths",
                numberOfMonths
        );


        request.setAttribute(
                "selectedPackage",
                gymPackage
        );


        request.setAttribute(
                "registrationType",
                "MEMBER"
        );


        request.setAttribute(
                "saunaSelected",
                false
        );


        request.setAttribute(
                "lockerSelected",
                false
        );
    }


    // =========================================================
    // CALCULATE WALK-IN
    // =========================================================
    private void calculateWalkIn(
            HttpServletRequest request)
            throws SQLException {

        List<GymService> selectedServices =
                getSelectedServices(
                        request
                );


        BigDecimal total =
                priceCalculator
                        .calculateWalkIn(
                                selectedServices
                        );


        request.setAttribute(
                "calculatedTotal",
                total
        );


        request.setAttribute(
                "registrationType",
                "WALK_IN"
        );


        setSelectedServiceAttributes(
                request,
                selectedServices
        );


        request.setAttribute(
                "selectedPackageId",
                null
        );


        request.setAttribute(
                "selectedMonths",
                null
        );


        request.setAttribute(
                "selectedPackage",
                null
        );
    }


    // =========================================================
    // SAVE
    // =========================================================
    private void save(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        String registrationType =
                request.getParameter(
                        "registrationType"
                );


        if ("MEMBER".equals(
                registrationType)) {

            saveMembership(
                    request,
                    response
            );

        } else if ("WALK_IN".equals(
                registrationType)) {

            saveWalkIn(
                    request,
                    response
            );

        } else {

            throw new IllegalArgumentException(
                    "Please select a registration type."
            );
        }
    }


    // =========================================================
    // SAVE MEMBERSHIP
    // =========================================================
    private void saveMembership(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        String packageValue =
                request.getParameter(
                        "packageId"
                );


        String monthsValue =
                request.getParameter(
                        "numberOfMonths"
                );


        if (packageValue == null
                || packageValue.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a membership package."
            );
        }


        if (monthsValue == null
                || monthsValue.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please enter number of months."
            );
        }


        int packageId =
                Integer.parseInt(
                        packageValue
                );


        int numberOfMonths =
                Integer.parseInt(
                        monthsValue
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


        BigDecimal total =
                priceCalculator
                        .calculateMembership(
                                gymPackage,
                                numberOfMonths
                        );


        // MEMBER
        Member member =
                new Member();


        member.setFullName(
                request.getParameter(
                        "fullName"
                )
        );


        member.setPhone(
                request.getParameter(
                        "phone"
                )
        );


        member.setEmail(
                request.getParameter(
                        "email"
                )
        );


        member.setGender(
                request.getParameter(
                        "gender"
                )
        );


        member.setRegistrationDate(
                LocalDate.now()
        );


        // TRANSACTION
        GymTransaction transaction =
                new GymTransaction();


        transaction.setCustomerType(
                "MEMBER"
        );


        transaction.setCustomerName(
                member.getFullName()
        );


        transaction.setPackageId(
                packageId
        );


        transaction.setNumberOfMonths(
                numberOfMonths
        );


        transaction.setWalkInFee(
                BigDecimal.ZERO
        );


        transaction.setTotalAmount(
                total
        );


        /*
         * Membership has no paid add-ons.
         */
        List<GymService> noServices =
                new ArrayList<>();


        transactionDAO.saveMembership(
                member,
                gymPackage,
                transaction,
                noServices,
                0
        );


        /*
         * IMPORTANT:
         * After saving, go directly
         * to Transaction Records.
         */
        response.sendRedirect(
                request.getContextPath()
                + "/transactions?created=1"
        );
    }


    // =========================================================
    // SAVE WALK-IN
    // =========================================================
    private void saveWalkIn(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        List<GymService> selectedServices =
                getSelectedServices(
                        request
                );


        BigDecimal total =
                priceCalculator
                        .calculateWalkIn(
                                selectedServices
                        );


        GymTransaction transaction =
                new GymTransaction();


        transaction.setCustomerType(
                "WALK_IN"
        );


        transaction.setCustomerName(
                request.getParameter(
                        "fullName"
                )
        );


        transaction.setMemberId(
                null
        );


        transaction.setPackageId(
                null
        );


        transaction.setNumberOfMonths(
                0
        );


        transaction.setWalkInFee(
                priceCalculator
                        .getWalkInFee()
        );


        transaction.setTotalAmount(
                total
        );


        transactionDAO.saveWalkIn(
                transaction,
                selectedServices,
                0
        );


        /*
         * IMPORTANT:
         * After saving, go directly
         * to Transaction Records.
         */
        response.sendRedirect(
                request.getContextPath()
                + "/transactions?created=1"
        );
    }


    // =========================================================
    // LOAD PACKAGES + SERVICES
    // =========================================================
    private void loadFormData(
            HttpServletRequest request)
            throws SQLException {

        request.setAttribute(
                "packages",
                gymDAO.getAllPackages()
        );


        request.setAttribute(
                "services",
                gymDAO.getAllServices()
        );
    }


    // =========================================================
    // GET PACKAGE BY ID
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


        throw new SQLException(
                "Membership package not found."
        );
    }


    // =========================================================
    // GET SELECTED WALK-IN SERVICES
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
                     * Only these services
                     * are valid for Walk-In.
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


    // =========================================================
    // PRESERVE WALK-IN CHECKBOXES
    // =========================================================
    private void setSelectedServiceAttributes(
            HttpServletRequest request,
            List<GymService> selectedServices) {

        boolean saunaSelected =
                false;

        boolean lockerSelected =
                false;


        for (GymService service : selectedServices) {


            if ("Sauna".equalsIgnoreCase(
                    service.getServiceName())) {

                saunaSelected =
                        true;
            }


            if ("Locker Rental".equalsIgnoreCase(
                    service.getServiceName())) {

                lockerSelected =
                        true;
            }
        }


        request.setAttribute(
                "saunaSelected",
                saunaSelected
        );


        request.setAttribute(
                "lockerSelected",
                lockerSelected
        );
    }


    // =========================================================
    // PRESERVE CUSTOMER INFORMATION
    // =========================================================
    private void preserveCustomerInformation(
            HttpServletRequest request) {

        request.setAttribute(
                "fullName",
                request.getParameter(
                        "fullName"
                )
        );


        request.setAttribute(
                "phone",
                request.getParameter(
                        "phone"
                )
        );


        request.setAttribute(
                "email",
                request.getParameter(
                        "email"
                )
        );


        request.setAttribute(
                "gender",
                request.getParameter(
                        "gender"
                )
        );
    }
}