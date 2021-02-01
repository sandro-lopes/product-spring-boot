package com.github.product.api.controllers;

import java.math.BigDecimal;
import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.product.api.entities.Product;
import com.github.product.api.exceptions.NotFoundException;
import com.github.product.api.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {

	private final ProductService service;
	
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
	
	@PostMapping (consumes = MediaType.APPLICATION_JSON_VALUE)
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
		
		Product product = service.find(id).orElseThrow(NotFoundException::new);
		
		return ResponseEntity
				.ok(product);
	}
	
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<Product> update(@PathVariable Integer id, @Valid @RequestBody Product product) {
		
		Product productUpdated = service.update(id, product);
		
		return ResponseEntity
				.ok()
				.body(productUpdated);
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> remove(@PathVariable Integer id) {
		
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
