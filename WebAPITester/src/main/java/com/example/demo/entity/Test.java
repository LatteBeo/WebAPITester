package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent a test.
 */
@Entity
@Getter
@Setter
public class Test {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(length = 200)
	@Size(max = 200)
	@NotBlank
	private String memo;
	@Column(length = 300)
	@Size(max = 300)
	@NotBlank
	private String endpointurl;
	@Column(length = 200)
	@Size(max = 200)
	@NotBlank
	private String apiname;
	@Column(length = 20)
	@NotBlank
	private String method;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "test", fetch = FetchType.EAGER)
	private List<TestParameter> testParameters;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "test", fetch = FetchType.EAGER)
	private List<TestAssertion> testAssertions;
	@OneToMany(mappedBy = "test", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<TestSetDetail> testSetDetail;
}