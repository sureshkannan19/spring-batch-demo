package com.subabk.cache;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.subabk.bo.Citizen;
import com.subabk.common.QueryService;
import com.subabk.readers.mapper.CitizenRowMapper;

import lombok.NonNull;

@Service
public class CitizenProxy {

	private JdbcTemplate jdbcTemplate;

	private QueryService queryService;

	private CacheManager cacheManager;

	public CitizenProxy(@NonNull @Qualifier("testJdbcTemplate") JdbcTemplate jdbcTemplate,
			@NonNull CacheManager cacheManager, @NonNull QueryService queryService) {
		this.cacheManager = cacheManager;
		this.queryService = queryService;
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Citizen> findCitizens() {
		List<Citizen> result = jdbcTemplate.query(queryService.retrieveAllPersonDetails(), new CitizenRowMapper());
		Map<Long, List<Citizen>> mapStateIdToCitizens = result.stream()
				.collect(Collectors.groupingBy(Citizen::getStateId));
		for (Entry<Long, List<Citizen>> citizens : mapStateIdToCitizens.entrySet()) {
			Cache cache = cacheManager.getCache("CitizenDetails");
			cache.put(citizens.getKey(), citizens.getValue());
		}
		return result;
	}
}
