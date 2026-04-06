package com.gabrielf.revendedora.repositories;


import com.gabrielf.revendedora.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {


}
