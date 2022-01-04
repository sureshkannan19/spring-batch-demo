package com.subabk.partitioner;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.subabk.util.JobExecutionContextHelper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class PollGridTasklet implements Tasklet, StepExecutionListener {

	@Autowired
	private JobExecutionContextHelper helper;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// do Nothing
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// Map<String, List<Long>> partitionGrid =
		// helper.getPartitionGrid(stepExecution);
		return stepExecution.getExitStatus();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Queue<Map<Long, List<Long>>> statesPerPartition = helper.getPartitionGrids(chunkContext);
		final Map<Long, List<Long>> currentPartitionGrid = statesPerPartition.poll();
		log.info("Next_Partition_Grid : {}", currentPartitionGrid);
		helper.setPartitionGrid(chunkContext, currentPartitionGrid);
		return RepeatStatus.FINISHED;
	}

}
