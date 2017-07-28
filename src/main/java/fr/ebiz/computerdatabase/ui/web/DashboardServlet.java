package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/home"})
public class DashboardServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/dashboard.jsp";
    private static final String COMPUTERS_ATTR = "computers";

    @Autowired
    private ComputerService computerService;


    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GetAllComputersRequest dashboardRequest = DashboardRequestParser.parseRequest(request, response);

        request.setAttribute(DashboardRequestParser.SEARCH_PARAM, dashboardRequest.getQuery());
        request.setAttribute(DashboardRequestParser.PAGE_SIZE_PARAM, dashboardRequest.getPageSize());
        request.setAttribute(DashboardRequestParser.PAGE_PARAM, dashboardRequest.getPage());
        request.setAttribute(DashboardRequestParser.SORT_COLUMN_PARAM, dashboardRequest.getColumn().name().toLowerCase());
        request.setAttribute(COMPUTERS_ATTR, computerService.getAll(dashboardRequest));

        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
