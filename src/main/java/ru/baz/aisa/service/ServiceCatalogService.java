package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.baz.aisa.entity.Service;
import ru.baz.aisa.exception.ApplicationException;
import ru.baz.aisa.repository.ServiceCatalogRepository;
import ru.baz.aisa.rest.response.ServiceResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ServiceHelperService serviceHelperService;

    @Transactional
    public Map<String, List<ServiceResponse>> getActiveServices() {
        List<Service> services = serviceCatalogRepository.findByActiveIsTrue();

        List<ServiceResponse> serviceResponses = services.stream()
                .map(serviceHelperService::map)
                .collect(Collectors.toList());

        return serviceResponses.stream().collect(Collectors.groupingBy(ServiceResponse::getName));
    }

    @Transactional
    public ServiceResponse addNewService(String name) {
        Service service = new Service();
        service.setName(name);
        service.setActive(true);

        try {
            serviceCatalogRepository.saveAndFlush(service);
        } catch (Exception ex) {
            throw new ApplicationException("Name " + name + " cannot be used");
        }
        return serviceHelperService.map(service);
    }

    @Transactional
    public void removeService(Long id) {
        try {
            Service service = serviceCatalogRepository.findById(id).orElse(null);
            serviceCatalogRepository.delete(service);
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @Transactional
    public List<Service> getServices(List<Long> serviceIds) {
        return serviceCatalogRepository.findByIdIn(serviceIds);
    }

}
