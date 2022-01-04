package com.subabk.listener;

import java.time.Duration;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.metrics.BatchMetrics;

import com.subabk.bo.Citizen;
import com.subabk.exception.CustomException;
import com.subabk.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StepListener implements StepExecutionListener, SkipListener<Citizen, Citizen> {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		stepExecution.setStartTime(TimeUtil.getCurrentDate());
		log.info("Executing Step : {} ", stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		stepExecution.setEndTime(TimeUtil.getCurrentDate());
		Duration timeTaken = BatchMetrics.calculateDuration(stepExecution.getStartTime(), stepExecution.getEndTime());
		log.info("{} Step completed and took {} ms.", stepExecution.getStepName(), timeTaken.toMillis());
		return stepExecution.getExitStatus();
	}

	@Override
	public void onSkipInRead(Throwable t) {
	}

	@Override
	public void onSkipInWrite(Citizen item, Throwable t) {
		String errorMessage = "Exception occured while inserting " + item;
		throw new CustomException(errorMessage, t);
	}

	@Override
	public void onSkipInProcess(Citizen item, Throwable t) {
		String errorMessage = "Exception occured while processing " + item;
		throw new CustomException(errorMessage, t);
	}

}
