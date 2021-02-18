package com.github.product.api.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.github.product.api.controllers.ProductController;
import com.github.product.api.entities.Product;
import com.github.product.api.models.ProductModel;

@Component
public class ProductAssembler extends RepresentationModelAssemblerSupport<Product, ProductModel> {

	@Autowired
	protected ModelMapper mapper;
	
	@Autowired
	private PagedResourcesAssembler<Product> pagedAssembler;
	
	public ProductAssembler() {
		super(ProductController.class, ProductModel.class);
	}

	@Override
	public ProductModel toModel(Product product) {
		return mapper.map(product, ProductModel.class)
				.add(linkTo(methodOn(ProductController.class).find(product.getId())).withSelfRel())
				.add(linkTo(methodOn(ProductController.class).search(null, null, null, null)).withRel("products"));
	}
	
	public PagedModel<ProductModel> toPagedModel(Page<Product> page) {
		return pagedAssembler.toModel(page, this);
	}

}
