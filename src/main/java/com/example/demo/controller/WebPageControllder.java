package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web page controller.
 */
@Controller
public class WebPageControllder {
	@GetMapping("/jsonedit")
	public String jsonedit() {
		return "jsonedit";
	}
}