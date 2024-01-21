package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent a test set
 */
@Entity
@Getter
@Setter
public class TestSet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(length = 300)
	private String name = "";
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "testSet", fetch = FetchType.EAGER)
	private List<TestSetDetail> testList;
}