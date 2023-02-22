package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.baz.aisa.entity.Service;
import ru.baz.aisa.rest.response.ServiceResponse;

@Component
@RequiredArgsConstructor
public class ServiceHelperService {

    private final SlotHelperService slotHelperService;

    public ServiceResponse map(Service service) {
        if (service == null) {
            return null;
        }
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setSlots(service.getSlots());
        response.setTime(slotHelperService.formattedDuration(service.getSlots()));

        return response;
    }
}
