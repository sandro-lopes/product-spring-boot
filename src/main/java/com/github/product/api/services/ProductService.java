package com.github.product.api.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.product.api.entities.Product;
import com.github.product.api.exceptions.BusinessException;
import com.github.product.api.exceptions.NotFoundException;
import com.github.product.api.repositories.ProductRepository;
import com.github.product.api.utils.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository repository;
	
	private final Message message;

	public Product create(Product product) {

		if (repository.existsByName(product.getName())) {
			throw new BusinessException(message.get("product.same.name.exists"));
		}

		return repository.saveAndFlush(product);
	}

	public Product update(Integer id, Product product) {

		if(!repository.existsById(id)) {
			throw new NotFoundException(message.get("product.id.not.exists", id));
		}

		if(repository.existsAnotherWithSameName(id, product.getName())) {
			throw new BusinessException(message.get("product.same.name.exists"));
		}

		product.setId(id);
		return repository.saveAndFlush(product);
	}
	
	public void delete(Integer id) {
		repository.deleteById(id);
	}

	public Optional<Product> find(Integer id) {
		return repository.findById(id);
	}

	public Page<Product> search(String name, BigDecimal price, Integer quantity, Pageable pageable) {
		Product product = Product.builder()
				.name(name)
				.price(price)
				.quantity(quantity)
				.build();
		Example<Product> example = Example.of(product);
		return repository.findAll(example, pageable);
	}

}
