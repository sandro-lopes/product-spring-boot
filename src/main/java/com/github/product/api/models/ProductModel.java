package com.github.product.api.models;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductModel extends RepresentationModel<ProductModel>{
	
	private Integer id;
	
	private String name;
	
	private BigDecimal price;
	
	private Integer quantity;

}
