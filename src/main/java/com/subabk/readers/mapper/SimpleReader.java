package com.subabk.readers.mapper;

import java.util.Iterator;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Component
@Slf4j
public class SimpleReader implements ItemReader<Long> {

	private Iterator<Long> items;

	public SimpleReader(Iterator<Long> items) {
		this.items = items;
	}

	@Override
	public Long read() throws Exception {
		log.info("Inside simpleReader.");
		if (!this.items.hasNext()) {
			return items.next();
		}
		return null; // If null not returned by ItemReader, indefinite loop will occur.
	}

}
