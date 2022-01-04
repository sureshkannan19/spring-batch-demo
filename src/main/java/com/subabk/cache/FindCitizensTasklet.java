package com.subabk.cache;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;

import com.subabk.bo.Citizen;

import lombok.NonNull;

@Configuration
public class FindCitizensTasklet implements Tasklet {

	private CitizenProxy proxy;

	public FindCitizensTasklet(@NonNull CitizenProxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Citizen> citizensFound = proxy.findCitizens();

		for (int i = 0; i < citizensFound.size(); i++) {
			contribution.incrementReadCount();
		}
		return RepeatStatus.FINISHED;
	}

}
