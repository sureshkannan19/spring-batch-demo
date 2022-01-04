package com.subabk.skip;

import org.springframework.batch.item.ItemProcessor;

import com.subabk.bo.Citizen;

public class SkipProcessor implements ItemProcessor<Citizen, Citizen> {

	@Override
	public Citizen process(Citizen item) throws Exception {
		if(item.getAadharNumber().equals(13L)) {
			throw new RuntimeException("I dont like number 13.");
		}
		return item;
	}

}
