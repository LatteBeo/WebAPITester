package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent an assertion.
 */
@Entity
@Getter
@Setter
public class TestAssertion {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne
	@JsonIgnore
	private Test test;
	@Lob
	private String xpath = "";
	@Lob
	private String expectedValue = "";
	@Transient
	private int fieldIndex;
}