package com.subabk.partitioner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class CitizenPartitioner implements Partitioner {

	private Map<Long, List<Long>> statesPerPartition;

	public CitizenPartitioner(Map<Long, List<Long>> statesPerPartition) {
		this.statesPerPartition = statesPerPartition;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> map = new LinkedHashMap<>();
		int i = 1;
		for (List<Long> states : statesPerPartition.values()) {
			for (Long state : states) {
				ExecutionContext ex = new ExecutionContext();
				ex.putLong("partition", state);
				map.put("parition-" + i, ex);
				i++;
			}
		}
		return map;
	}

}
