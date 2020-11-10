package com.github.product.api.controllers;

import java.math.BigDecimal;
import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.product.api.entities.Product;
import com.github.product.api.exceptions.ResourceNotFoundException;
import com.github.product.api.services.ProductService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

	@Autowired
	private ProductService service;
	
	@GetMapping
	public ResponseEntity<Page<Product>> search(
			@RequestParam(value = "name", required = false) final String name,
			@RequestParam(value = "price", required = false) final BigDecimal price,
			@RequestParam(value = "quantity", required = false) final Integer quantity,
			@PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable) {
		
		Page<Product> page = service.search(name, price, quantity, pageable);
		
		return ResponseEntity
				.ok(page);
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<Product> create(@Valid @RequestBody Product product) {

		Product newProduct = service.create(product);
		
		URI uri = WebMvcLinkBuilder
				.linkTo(ProductController.class)
				.slash(newProduct.getId())
				.toUri();

		return ResponseEntity
				.created(uri)
				.body(newProduct);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> find(@PathVariable("id") Integer id) {
		
		Product product = service.find(id).orElseThrow(ResourceNotFoundException::new);
		
		return ResponseEntity
				.ok(product);
		
	}
	
	@PutMapping("id")
	@Transactional
	public ResponseEntity<Void> update(@Valid @RequestBody Product product) {
		
		service.update(product);
		
		return ResponseEntity
				.noContent()
				.build();
	}
}
