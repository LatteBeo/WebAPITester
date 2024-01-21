package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent a test result
 */
@Entity
@Getter
@Setter
public class TestResult {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne(fetch = FetchType.EAGER)
	private Test test;
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "testResult")
	private List<TestAssertResult> testAssertResult;
	@Lob
	private String requestUrl;
	@Lob
	private String response;
	private boolean result;
	@OneToMany
	private List<TestAssertResult> assertResult;
	private LocalDateTime execDateTime;
}