package com.subabk.partitioner;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.subabk.bo.Citizen;

@Component
public class PersonWriter implements ItemWriter<Citizen> {

	@Autowired
	@Qualifier("testJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Override
	public void write(List<? extends Citizen> items) throws Exception {

		String query = "INSERT INTO person (name, age, gender, aadharNumber, address, state_id) "
				+ " VALUES (?, ?, ?, ?, ?, ?)";

		this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				final Citizen citzen = items.get(i);
				ps.setString(1, citzen.getName());
				ps.setLong(2, citzen.getAge());
				ps.setString(3, citzen.getGender());
				ps.setLong(4, citzen.getAadharNumber());
				ps.setString(5, citzen.getAddress());
				ps.setLong(6, citzen.getStateId());
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});

	}

}
