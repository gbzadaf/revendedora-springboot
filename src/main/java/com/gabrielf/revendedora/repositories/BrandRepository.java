package com.gabrielf.revendedora.repositories;

import com.gabrielf.revendedora.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {


}
