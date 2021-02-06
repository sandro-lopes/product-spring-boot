package com.github.product.api.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.product.api.entities.Product;
import com.github.product.api.exceptions.BusinessException;
import com.github.product.api.exceptions.NotFoundException;
import com.github.product.api.repositories.ProductRepository;
import com.github.product.api.utils.Message;

@ExtendWith(SpringExtension.class)
@DisplayName("ProductService unit tests")
class ProductServiceTest {
	
	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	@Mock
	private Message message;
	
	private Product product;
	
	private Integer idDefault;
	
	@BeforeEach
	public void setUp() {
		
		product = Product.builder()
				.name("Maizena Cookie")
				.price(new BigDecimal(0.99))
				.quantity(5)
				.build();
		
		idDefault = 1;
	}
	
	@Test
	@DisplayName("It must create a product")
	public void createProductWithUnrepeatedName() {
		
		Mockito.when(repository.existsByName(product.getName())).thenReturn(false);
		Mockito.when(repository.saveAndFlush(product)).thenReturn(product);
		
		service.create(product);
		
		Mockito.verify(repository).saveAndFlush(product);
	}
	
	@Test
	@DisplayName("It must not create a product with repeated name")
	public void createProductWithRepeatedName() {
		
		Mockito.when(repository.existsByName(product.getName())).thenReturn(true);
		Assertions.assertThrows(BusinessException.class, () -> service.create(product));
	}
	
	@Test
	@DisplayName("It must update an existing and non repeated product")
	public void updateProduct() {
		Mockito.when(repository.existsById(idDefault)).thenReturn(true);
		Mockito.when(repository.existsAnotherWithSameName(idDefault, product.getName())).thenReturn(false);
		Mockito.when(repository.saveAndFlush(product)).thenReturn(product);
		
		Product productUpdated = service.update(idDefault, product);

		Mockito.verify(repository).saveAndFlush(product);
		assertThat(productUpdated.getId(), is(idDefault));
	}
	
	@Test
	@DisplayName("It must not update an inexisting product")
	public void updateRepeatedProduct() {
		Mockito.when(repository.existsById(idDefault)).thenReturn(false);
		Assertions.assertThrows(NotFoundException.class, () -> service.update(idDefault, product));
	}
	
	@Test
	@DisplayName("It must not update a repeated product")
	public void updateInexistingProduct() {
		Mockito.when(repository.existsById(idDefault)).thenReturn(true);
		Mockito.when(repository.existsAnotherWithSameName(idDefault, product.getName())).thenReturn(true);
		Assertions.assertThrows(BusinessException.class, () -> service.update(idDefault, product));
	}
	
	@Test
	@DisplayName("It must delete a product by ID")
	public void deleteById() {
		service.delete(idDefault);
		Mockito.verify(repository).deleteById(idDefault);
	}
	
	@Test
	@DisplayName("It must find a product by ID")
	public void findById() {
		Mockito.when(repository.findById(idDefault)).thenReturn(Optional.of(new Product()));
		service.find(idDefault);
	}
	
	@Test
	@DisplayName("It must search products")
	public void search() {
		Pageable pageable = Pageable.unpaged();
		Mockito.when(repository.findAll(pageable)).thenReturn(Page.empty());
		service.search(product.getName(), product.getPrice(), product.getQuantity(), pageable);
	}
}
