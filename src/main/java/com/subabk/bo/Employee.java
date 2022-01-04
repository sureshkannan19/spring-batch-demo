package com.subabk.bo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Employee implements Comparable<Employee> {

	private String name;
	private int age;
	private int exp;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee(String name, int age, int exp) {
		super();
		this.name = name;
		this.age = age;
		this.exp = exp;
	}

	public Employee() {
	}

	@Override
	public int compareTo(Employee emp) {
		return Integer.valueOf(this.getAge()).compareTo(emp.getAge());
	}

	public static void main(String[] args) {
		Employee e1 = new Employee("Kemp", 24, 2);
		Employee e2 = new Employee("Sandhya", 20, 0);
		Employee e3 = new Employee("Anil", 22, 3);
		Employee e4 = new Employee("Kumar", 30, 6);
		Employee e5 = new Employee("Tim", 32, 7);
		List<Employee> elist = Arrays.asList(e1, e2, e4, e3, e5);
		Collections.sort(elist);
		System.out.println(elist.get(0).name);
	}

}
