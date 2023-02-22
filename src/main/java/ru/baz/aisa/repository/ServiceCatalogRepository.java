package ru.baz.aisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.baz.aisa.entity.Service;

import java.util.List;

public interface ServiceCatalogRepository extends JpaRepository<Service, Long> {

    List<Service> findByActiveIsTrue();

    List<Service> findByIdIn(List<Long> serviceIds);
}
