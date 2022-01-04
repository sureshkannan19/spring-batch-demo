package com.subabk.readers.mapper;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public class SimpleProcessor implements ItemProcessor<Long, Long>{

	@Override
	public Long process(Long item) throws Exception {
		System.out.println("count");
		return item;
	}

}
