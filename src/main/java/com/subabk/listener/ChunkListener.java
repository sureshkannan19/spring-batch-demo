package com.subabk.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChunkListener {

	@BeforeChunk
	public void beforeChunk(ChunkContext chunkContext) {
		log.info("Before Executing {} ", chunkContext.getStepContext().getStepName());
	}

	@AfterChunk
	public void afterChunk(ChunkContext chunkContext) {
		log.info("After Executing {} ", chunkContext.getStepContext().getStepName());
	}
}
