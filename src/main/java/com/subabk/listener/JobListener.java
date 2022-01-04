package com.subabk.listener;

import java.time.Duration;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.metrics.BatchMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.subabk.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobListener implements JobExecutionListener {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		jobExecution.setStartTime(TimeUtil.getCurrentDate());
		log.info("Executing Job : {} ", jobExecution.getJobInstance().getJobName());
//		String text = String.format("JobStatus %s.", jobExecution.getStatus());
//		String subject = String.format("Information About Job %s", jobExecution.getJobInstance().getJobName());
//		SimpleMailMessage simpleMessage = getMail(subject, text);
//		mailSender.send(simpleMessage);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
//		String text = String.format("JobStatus %s.", jobExecution.getStatus());
//		String subject = String.format("Information Job %s", jobExecution.getJobInstance().getJobName());
//		SimpleMailMessage simpleMessage = getMail(subject, text);
//		mailSender.send(simpleMessage);

		jobExecution.setEndTime(TimeUtil.getCurrentDate());
		Duration timeTaken = BatchMetrics.calculateDuration(jobExecution.getStartTime(), jobExecution.getEndTime());
		log.info("{} Job completed and took {} ms.", jobExecution.getJobInstance().getJobName(), timeTaken.toMillis());
	}

	private SimpleMailMessage getMail(String subject, String text) {
		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo("sureshbabk19698@gmail.com");
		sm.setSubject(subject);
		sm.setText(text);
		return sm;
	}
}
