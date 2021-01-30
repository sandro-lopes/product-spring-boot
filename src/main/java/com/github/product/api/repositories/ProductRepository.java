package com.github.product.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.product.api.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
	
	boolean existsByName(String name);
	
	boolean existsById(Integer id);

	@Query("select case when count(p) > 0 then true else false end from Product p where lower(p.name) like lower(:name) and p.id <> :id")
	boolean existsAnotherWithSameName(Integer id, String name);
}
