package com.scarborough.nas.dao;



import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.scarborough.nas.pojo.User;
import com.scarborough.nas.util.TxDBUtils;

public class IUserDao {

	public int addUser(User user) {
		QueryRunner queryRunner = new QueryRunner(TxDBUtils.getSource());
		String sql = "insert into nas_user values(?,?,?,?,?,?,?,?)";
		try {
			return queryRunner.update(sql, user.getId(), user.getName(), user.getCar_plate(), user.getCar_mileage(),
					user.getPhone(), user.getCar_insurance(), user.getCar_model(), user.getRemarks());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;

	}
}
