package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Represent an endpoint.
 */
@Entity
@Getter
@Setter
public class Endpoint implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(length = 200)
	@Size(max = 200)
	@NotBlank
	private String name = "";
	@Column(length = 300)
	@Size(max = 300)
	@NotBlank
	private String url = "";
}