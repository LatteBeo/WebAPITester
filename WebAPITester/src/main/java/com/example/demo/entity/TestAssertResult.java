package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent an assertion result.
 */
@Entity
@Getter
@Setter
public class TestAssertResult {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Lob
	private String xpath = "";
	@Lob
	private String expectedValue = "";
	@Lob
	private String actualValue = "";
	private boolean result;
	@ManyToOne
	private TestResult testResult;
}