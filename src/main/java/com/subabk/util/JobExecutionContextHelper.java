package com.subabk.util;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class JobExecutionContextHelper {

	private static final String PARTITION_GRIDS = "PARTITION_GRIDS";
	private static final String PARTITION_GRID = "PARTITION_GRID";

	public void setPartitionGrids(ChunkContext chunkContext, Queue<Map<Long, List<Long>>> statesPerPartition) {
		log.info("****Storing PartitionGrid****[{}] :", statesPerPartition);
		getJobExecutionContext(chunkContext).put(PARTITION_GRIDS, statesPerPartition);
	}

	private ExecutionContext getJobExecutionContext(ChunkContext chunkContext) {
		return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
	}

	private ExecutionContext getJobExecutionContext(StepExecution stepExecution) {
		return stepExecution.getJobExecution().getExecutionContext();
	}

	public Queue<Map<Long, List<Long>>> getPartitionGrids(ChunkContext chunkContext) {
		return (Queue<Map<Long, List<Long>>>) getJobExecutionContext(chunkContext).get(PARTITION_GRIDS);
	}

	public Queue<Map<Long, List<Long>>> getPartitionGrids(JobExecution jobexecution) {
		return (Queue<Map<Long, List<Long>>>) jobexecution.getExecutionContext().get(PARTITION_GRIDS);
	}

	public void setPartitionGrid(ChunkContext chunkContext, Map<Long, List<Long>> currentPartitionGrid) {
		getJobExecutionContext(chunkContext).put(PARTITION_GRID, currentPartitionGrid);
	}

	public Map<Long, List<Long>> getPartitionGrid(StepExecution stepExecution) {
		return (Map<Long, List<Long>>) getJobExecutionContext(stepExecution).get(PARTITION_GRID);
	}
}
