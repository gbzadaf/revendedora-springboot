package com.gabrielf.revendedora.repositories;



import com.gabrielf.revendedora.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {


}
