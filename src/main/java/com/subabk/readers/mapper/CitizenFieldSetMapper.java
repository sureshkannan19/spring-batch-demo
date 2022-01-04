package com.subabk.readers.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.subabk.bo.Citizen;

public class CitizenFieldSetMapper implements FieldSetMapper<Citizen> {

	@Override
	public Citizen mapFieldSet(FieldSet fs) throws BindException {
		return Citizen.builder()
				.name(fs.readString("name"))
				.age(fs.readInt("age"))
				.gender(fs.readString("gender"))
				.aadharNumber(fs.readLong("aadharNumber"))
				.address(fs.readString("address"))
				.build();
	}

}
