package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.baz.aisa.entity.Service;
import ru.baz.aisa.repository.ServiceCatalogRepository;
import ru.baz.aisa.rest.response.ServiceResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ServiceHelperService serviceHelperService;

    @Transactional
    public List<ServiceResponse> getActiveServices() {
        List<Service> services = serviceCatalogRepository.findByActiveIsTrue();

        return services.stream()
                .map(serviceHelperService::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceResponse addNewService(String name) {
        Service service = new Service();
        service.setName(name);
        service.setActive(true);

        try {
            serviceCatalogRepository.saveAndFlush(service);
        } catch (Exception ex) {
            throw new RuntimeException("Name " + name + " cannot be used");
        }
        return serviceHelperService.map(service);
    }

    @Transactional
    public void removeService(Long id) {
        try {
            Service service = serviceCatalogRepository.findById(id).orElse(null);
            serviceCatalogRepository.delete(service);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    public List<Service> getServices(List<Long> serviceIds) {
        return serviceCatalogRepository.findByIdIn(serviceIds);
    }

}
