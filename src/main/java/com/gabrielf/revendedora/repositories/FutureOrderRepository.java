package com.gabrielf.revendedora.repositories;



import com.gabrielf.revendedora.model.FutureOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FutureOrderRepository extends JpaRepository<FutureOrder, UUID> {



}
