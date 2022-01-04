package com.subabk.restart;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StatefulItemReader implements ItemStreamReader<Long> {

	private List<Long> items;
	private int curIndex = -1;
	private boolean isRestart = false;

	public StatefulItemReader(List<Long> items) {
		this.items = items;
		this.curIndex = 0;
	}

	@Override
	public Long read() throws Exception {
		Long item = null;
		if (this.curIndex < this.items.size()) {
			item = this.items.get(this.curIndex);
			this.curIndex++;
		}
		if (this.curIndex == 13 && !isRestart) {
			throw new RuntimeException("I don't like number 13.");
		}
		return item; // If null not returned by ItemReader, indefinite loop will occur.
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey("curIndex")) {
			// After failure, to pick from where it failed
			this.curIndex = executionContext.getInt("curIndex");
			this.isRestart = true;
		} else {
			// Beginning of the job
			this.curIndex = 0;
			executionContext.put("curIndex", this.curIndex);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// updates every transaction
		executionContext.put("curIndex", this.curIndex);
	}

	@Override
	public void close() throws ItemStreamException {

	}

}
