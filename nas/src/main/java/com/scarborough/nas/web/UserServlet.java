package com.scarborough.nas.web;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scarborough.nas.dao.IUserDao;

@WebServlet("/admin/login")
public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = -4069665680130980126L;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		IUserDao userDao = new IUserDao();
		String name = req.getParameter("fullname");
//		int phone = req.getParameter("mobile");
		String car_plate = req.getParameter("isCarNo");
		String car_mileage = req.getParameter("mileage");
		String car_insurance = req.getParameter("chdate");
		String car_model = req.getParameter("carbrand");
		String remarks = req.getParameter("detail");
//		User user = new User(null, name, phone, car_plate, car_mileage, car_insurance, car_model, remarks);
		super.doPost(req, resp);
	}
}
