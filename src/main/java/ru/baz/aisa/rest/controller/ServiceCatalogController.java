package ru.baz.aisa.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.baz.aisa.config.ApplicationProperties;
import ru.baz.aisa.rest.request.ServiceRequest;
import ru.baz.aisa.rest.response.ServiceResponse;
import ru.baz.aisa.service.ServiceCatalogService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ServiceCatalogController {
    private final ServiceCatalogService serviceCatalogService;

    private final ApplicationProperties applicationProperties;

    @ApiOperation(value = "Get list of services", notes = "List of all services")
    @GetMapping("/services")
    public ResponseEntity<Map<String, List<ServiceResponse>>> getServiceList() {
        Map<String, List<ServiceResponse>> serviceList = serviceCatalogService.getActiveServices();

        return new ResponseEntity<>(serviceList, HttpStatus.OK);
    }

    @ApiOperation(value = "Add new service", notes = "Return created service")
    @PutMapping("/service")
    public  ResponseEntity<ServiceResponse> addService(@RequestBody ServiceRequest request,
                                                       @RequestParam String token) {
        if (StringUtils.isBlank(token) || !token.equals(applicationProperties.getToken())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ServiceResponse response = serviceCatalogService.addNewService(request.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Remove the service", notes = "Return http status")
    @DeleteMapping("/service/{id}")
    public  ResponseEntity<ServiceResponse> removeService(@PathVariable Long id) {

        serviceCatalogService.removeService(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
