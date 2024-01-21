package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent a parameter of test
 */
@Entity
@Getter
@Setter
public class TestParameter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(length = 300)
	@Size(max = 300)
	private String name = "";
	@Column(length = 300)
	private String value = "";
	@Lob
	@JsonIgnore
	private byte[] file;
	@Column(length = 300)
	private String fileName = "";
	@ManyToOne
	@JsonIgnore
	private Test test;
	@Transient
	@JsonIgnore
	private int fieldIndex;
}