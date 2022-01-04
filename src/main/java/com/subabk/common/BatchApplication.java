package com.subabk.common;

import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;

import com.subabk.cache.CacheBatchApplication;
import com.subabk.cache.CacheBatchConfiguration;
import com.subabk.jobdecider.JobDeciderBatchApplication;
import com.subabk.jobdecider.JobDeciderBatchConfiguration;
import com.subabk.parent.ParentBatchApplication;
import com.subabk.parent.ParentBatchConfiguration;
import com.subabk.partitioner.PartitionerBatchApplication;
import com.subabk.partitioner.PartitionerBatchConfiguration;
import com.subabk.readers.ReaderBatchApplication;
import com.subabk.readers.ReaderBatchConfiguration;
import com.subabk.restart.StatefulBatchApplication;
import com.subabk.restart.StatefulBatchConfiguration;
import com.subabk.skip.SkipBatchApplication;
import com.subabk.skip.SkipBatchConfiguration;
import com.subabk.writers.WriterBatchApplication;
import com.subabk.writers.WriterBatchConfiguration;

@Configuration
public class BatchApplication {

	public static void main(String[] args) {
		String jobName = getJobName(args);
		if (ParentBatchConfiguration.JOB_NAME.equals(jobName)) {
			ParentBatchApplication.main(args);
		} else if (JobDeciderBatchConfiguration.JOB_NAME.equals(jobName)) {
			JobDeciderBatchApplication.main(args);
		} else if (ReaderBatchConfiguration.JOB_NAME.equals(jobName)) {
			ReaderBatchApplication.main(args);
		} else if (StatefulBatchConfiguration.JOB_NAME.equals(jobName)) {
			StatefulBatchApplication.main(args);
		} else if (WriterBatchConfiguration.JOB_NAME.equals(jobName)) {
			WriterBatchApplication.main(args);
		} else if (SkipBatchConfiguration.JOB_NAME.equals(jobName)) {
			SkipBatchApplication.main(args);
		} else if (PartitionerBatchConfiguration.JOB_NAME.equals(jobName)) {
			PartitionerBatchApplication.main(args);
		} else if (CacheBatchConfiguration.JOB_NAME.equals(jobName)) {
			CacheBatchApplication.main(args);
		} 
		
	}

	private static String getJobName(String[] args) {
		for (String params : args) {
			String[] jobName = params.split(Pattern.quote("spring.batch.job.names="));
			if (jobName.length > 1) {
				return jobName[1];
			}
		}
		return null;
	}

}
