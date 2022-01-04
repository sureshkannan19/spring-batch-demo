package com.subabk.bo;

import java.io.Serializable;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Citizen implements Serializable, ResourceAware {

	private static final long serialVersionUID = 5731320156506870065L;

	private String name;
	private int age;
	private String gender;
	private String address;
	private int mobileNum;
	private Long aadharNumber;
	private Long stateId;
	private String resourceLocation;

	@Override
	public void setResource(Resource resource) {
		this.resourceLocation = resource.getDescription();
	}
}
