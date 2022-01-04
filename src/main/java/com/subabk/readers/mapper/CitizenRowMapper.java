package com.subabk.readers.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.subabk.bo.Citizen;

public class CitizenRowMapper implements RowMapper<Citizen> {

	@Override
	public Citizen mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Citizen.builder()
				.name(rs.getString("name"))
				.age(rs.getInt("age"))
				.gender(rs.getString("gender"))
				.aadharNumber(rs.getLong("aadharNumber"))
				.address(rs.getString("address"))
				.stateId(rs.getLong("state_id"))
				.build();
	}

}
