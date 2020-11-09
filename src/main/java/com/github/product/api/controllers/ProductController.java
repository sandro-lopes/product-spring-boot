package com.github.product.api.controllers;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.product.api.entities.Product;
import com.github.product.api.services.ProductService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

	@Autowired
	private ProductService service;
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> find(@PathVariable("id") UUID id) {
		
		Product product = service.find(id);
		
		return ResponseEntity
				.ok(product);
		
	}
	
	@GetMapping
	public ResponseEntity<Page<Product>> search() {
		
		Page<Product> page = service.search();
		
		return ResponseEntity
				.ok(page);
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<Product> create(@Valid @RequestBody Product product) {

		service.create(product);

		return ResponseEntity
				.created(null)
				.body(product);
	}
	
	@PutMapping("id")
	@Transactional
	public HeadersBuilder<?> update(@Valid @RequestBody Product product) {
		
		service.update(product);
		
		return ResponseEntity
				.noContent();
	}
}