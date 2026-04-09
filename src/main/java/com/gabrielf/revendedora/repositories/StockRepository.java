package com.gabrielf.revendedora.repositories;



import com.gabrielf.revendedora.model.Product;
import com.gabrielf.revendedora.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByProduct(Product product);
}
