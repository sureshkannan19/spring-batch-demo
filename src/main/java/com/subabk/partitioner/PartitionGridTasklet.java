package com.subabk.partitioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Lists;
import com.subabk.util.JobExecutionContextHelper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class PartitionGridTasklet implements Tasklet {

	@Value("${app.gridsize}")
	private int gridSize;

	@Autowired
	@Qualifier("testJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JobExecutionContextHelper helper;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String query = "Select state_id from ref_citizen_loc";
		List<Long> totalStates = jdbcTemplate.queryForList(query, Long.class);
		log.info("Total States found : {} ", totalStates);

		Queue<Map<Long, List<Long>>> statesPerPartition = new LinkedList<>();
		
		// determine how many states fall under single grid
		List<List<Long>> partitions = Lists.partition(new ArrayList<>(totalStates), gridSize);
		long i = 1;
		for (List<Long> states : partitions) {
			Map<Long, List<Long>> statesPerGrid = new HashMap<>();
			statesPerGrid.put(i, new ArrayList<>(states));
			statesPerPartition.add(statesPerGrid);
			i++;
		}
		helper.setPartitionGrids(chunkContext, statesPerPartition);
		return RepeatStatus.FINISHED;
	}

}
