package com.subabk.writers;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.classify.Classifier;

import com.subabk.bo.Citizen;

public class ClassifierItemWriter implements Classifier<Citizen, ItemWriter<? super Citizen>> {

	private static final long serialVersionUID = -2825342716355310830L;

	private ItemWriter<Citizen> evenAadharIdCitizen;
	private ItemWriter<Citizen> oddAadharIdCitizen;

	public ClassifierItemWriter(StaxEventItemWriter<Citizen> xmlFileItemWriter,
			FlatFileItemWriter<Citizen> flatFileItemWriter) {
		this.evenAadharIdCitizen = xmlFileItemWriter;
		this.oddAadharIdCitizen = flatFileItemWriter;
	}

	@Override
	public ItemWriter<? super Citizen> classify(Citizen citizen) {
		return citizen.getAadharNumber() % 2 == 0 ? evenAadharIdCitizen : oddAadharIdCitizen;
	}

}
