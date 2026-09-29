package com.omnicare.modules.client.repository;

import com.omnicare.modules.client.model.Client;
import com.omnicare.modules.client.model.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    List<Client> findByStatus(ClientStatus status);
}