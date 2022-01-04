package com.subabk.common;

import org.springframework.stereotype.Component;

@Component
public class QueryService {

	public String retrieveAllCitizensDetails() {
		return "Select name, age, gender, aadharNumber, address, state_id FROM citizen";
	}

	public String retrieveCitizensDetailsByAadharNumber() {
		return "SELECT * FROM CITIZEN where AadharNumber = ?;";
	}

	public String deleteCitizensByAadharNumber() {
		return "DELETE FROM CITIZEN where AadharNumber = ?;";
	}

	public String retrieveAdultCitizens() {
		return "SELECT * FROM TEST.CITIZEN where age > 18;";
	}
	public String retrieveAllPersonDetails() {
		return "Select name, age, gender, aadharNumber, address, state_id FROM person";
	}
}
