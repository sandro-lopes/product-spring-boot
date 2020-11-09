package com.github.product.api.services;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.product.api.entities.Product;
import com.github.product.api.repositories.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;

	public Product create(@Valid Product product) {
		// TODO check if there is product with the same name.
		return repository.save(product);
	}
	
	public Product update(@Valid Product product) {
		// TODO check if there is another product (different id) with the same name.
		return repository.save(product);
	}

	public Optional<Product> find(Integer id) {
		return repository.findById(id);
	}

	public Page<Product> search(String name, BigDecimal price, Integer quantity, Pageable pageable) {
		Example<Product> example = Example.of(new Product(name, price, quantity));
		return repository.findAll(example, pageable);
	}
}
