package com.example.demo.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent each detail of test set.
 */
@Entity
@Getter
@Setter
public class TestSetDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne(fetch = FetchType.EAGER)
	private Test test;
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnore
	private TestSet testSet;
	@JsonIgnore
	@Transient
	private int fieldIndex;
	@JsonIgnore
	@Transient
	private String testid;
}