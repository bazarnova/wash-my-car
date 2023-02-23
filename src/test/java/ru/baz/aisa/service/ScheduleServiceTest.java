package ru.baz.aisa.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.baz.aisa.config.ApplicationProperties;
import ru.baz.aisa.rest.request.ScheduleServiceRequest;
import ru.baz.aisa.rest.request.ServiceRequest;
import ru.baz.aisa.rest.response.ServiceResponse;
import ru.baz.aisa.rest.response.SlotStepResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class ScheduleServiceTest {

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SlotHelperService slotHelperService;

    @Autowired
    ApplicationProperties applicationProperties;

    @Value("${local.server.port}")
    int port;

    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1");

    static {
        postgreSQLContainer.start();
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USER", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @BeforeEach
    void cleanSchedule() {
        jdbcTemplate.execute(" TRUNCATE TABLE wash_my_car.schedule");
    }

    @Test
    void addNewSlotAndReturnTrue() {
        LocalDate date = LocalDate.now().plusDays(1);

        ScheduleServiceRequest request = new ScheduleServiceRequest();
        request.setUserName("Ivanov");
        request.setUserPhone("012345678901");
        request.setDate(date);
        request.setStartSlot(60);
        request.setServiceIds(Arrays.asList(2L, 3L));

        String url = "http://localhost:" + port;

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url + "/schedule", HttpMethod.PUT,
                new HttpEntity<>(request), Map.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map<LocalDate, List<SlotStepResponse>> body = (Map<LocalDate, List<SlotStepResponse>>)responseEntity.getBody();
        List<SlotStepResponse> slots = body.get(date.toString());
        Assertions.assertEquals(5, slots.size());
    }

    @Test
    void addNewSlotInThePastAndReturnException() {
        ScheduleServiceRequest request = new ScheduleServiceRequest();
        request.setUserName("Ivanov");
        request.setUserPhone("012345678901");
        request.setDate(LocalDate.now().minusDays(1));
        request.setStartSlot(60);
        request.setServiceIds(Arrays.asList(2L, 3L));

        String url = "http://localhost:" + port;

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url + "/schedule", HttpMethod.PUT,
                new HttpEntity<>(request), Map.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addNewSlotToBusySlotAndReturnException() {
        LocalDate date = LocalDate.now().plusDays(1);

        ScheduleServiceRequest firstRequest = new ScheduleServiceRequest();
        firstRequest.setUserName("Ivanov");
        firstRequest.setUserPhone("012345678901");
        firstRequest.setDate(date);
        firstRequest.setStartSlot(60);
        firstRequest.setServiceIds(Arrays.asList(2L, 3L));

        String url = "http://localhost:" + port;

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(firstRequest), Map.class);
        ScheduleServiceRequest secondRequest = new ScheduleServiceRequest();
        secondRequest.setUserName("Petrov");
        secondRequest.setUserPhone("012345678902");
        secondRequest.setDate(date);
        secondRequest.setStartSlot(60);
        secondRequest.setServiceIds(Collections.singletonList(2L));

        ResponseEntity<Map> entity = restTemplate.exchange(url + "/schedule",
                HttpMethod.PUT, new HttpEntity<>(secondRequest), Map.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

    @Test
    void addNewSlotOverlapsOtherSlotsAndReturnException() {
        LocalDate date = LocalDate.now().plusDays(1);
        String url = "http://localhost:" + port;

        ScheduleServiceRequest firstRequest = new ScheduleServiceRequest();
        firstRequest.setUserName("Ivanov");
        firstRequest.setUserPhone("012345678901");
        firstRequest.setDate(date);
        firstRequest.setStartSlot(60);
        firstRequest.setServiceIds(Arrays.asList(2L, 3L));

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(firstRequest), Map.class);

        ScheduleServiceRequest secondRequest = new ScheduleServiceRequest();
        secondRequest.setUserName("Petrov");
        secondRequest.setUserPhone("012345678902");
        secondRequest.setDate(date);
        secondRequest.setStartSlot(68);
        secondRequest.setServiceIds(Collections.singletonList(2L));

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(secondRequest), Map.class);

        ScheduleServiceRequest thirdRequest = new ScheduleServiceRequest();
        thirdRequest.setUserName("Sidorov");
        thirdRequest.setUserPhone("012345678903");
        thirdRequest.setDate(date);
        thirdRequest.setStartSlot(66);
        thirdRequest.setServiceIds(Arrays.asList(2L, 3L));

        ResponseEntity<Map> entity = restTemplate.exchange(url + "/schedule",
                HttpMethod.PUT, new HttpEntity<>(thirdRequest), Map.class);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

    @Test
    void getAvailableSlotsReturnTrue() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Long> serviceIds = Arrays.asList(2L, 3L, 2L, 2L);
        Integer startSlot = 10;

        ScheduleServiceRequest request = new ScheduleServiceRequest();
        request.setUserName("Ivanov");
        request.setUserPhone("012345678901");
        request.setDate(date);
        request.setStartSlot(startSlot);
        request.setServiceIds(serviceIds);

        String url = "http://localhost:" + port;

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(request), Map.class);

        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url + "/schedule?days=2", Map.class);

        List<Integer> emptySlots = slotHelperService.getSlotsForAllDay();
        emptySlots.remove(startSlot);
        for (Long serviceId : serviceIds) {

            Integer slots = jdbcTemplate.queryForObject(
                    "SELECT slots FROM wash_my_car.service where id =" + serviceId, Integer.class);
            startSlot += slots;
            emptySlots.remove(startSlot);
        }

        Assertions.assertEquals(emptySlots.size(), ((List) responseEntity.getBody().get(date.toString())).size());
    }

    @Test
    void getAllSlotsForCurrentUserReturnTrue() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Long> serviceIds = Arrays.asList(2L, 3L);

        ScheduleServiceRequest firstRequest = new ScheduleServiceRequest();
        firstRequest.setUserName("Ivanov");
        firstRequest.setUserPhone("012345678901");
        firstRequest.setDate(date);
        firstRequest.setStartSlot(10);
        firstRequest.setServiceIds(serviceIds);

        String url = "http://localhost:" + port;

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(firstRequest), Map.class);

        String userName = "Ivanov";
        String phone = "012345678901";

        ScheduleServiceRequest secondRequest = new ScheduleServiceRequest();
        secondRequest.setUserName(userName);
        secondRequest.setUserPhone(phone);
        secondRequest.setDate(date.plusDays(1));
        secondRequest.setStartSlot(20);
        secondRequest.setServiceIds(serviceIds);

        restTemplate.exchange(url + "/schedule", HttpMethod.PUT, new HttpEntity<>(secondRequest), Map.class);

        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM wash_my_car.user where name = '" + userName + "' and phone = '" + phone + "'", Long.class);

        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url + "/schedule/user/" + userId, Map.class);

        Assertions.assertEquals(2, responseEntity.getBody().size());
        Assertions.assertEquals(5, ((List) responseEntity.getBody().get(date.toString())).size());
        Assertions.assertEquals(5, ((List) responseEntity.getBody().get(date.plusDays(1).toString())).size());
    }

    @Test
    void addServiceWithTrueTokenReturnTrue() {
        String url = "http://localhost:" + port;

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setName("Тонировка зеркала заднего вида");
        serviceRequest.setSlots(1);

        ResponseEntity<ServiceResponse> response = restTemplate.exchange(url + "/service?token=" + applicationProperties.getToken(),
                HttpMethod.PUT, new HttpEntity<>(serviceRequest), ServiceResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addServiceWithFalseTokenReturnStatusForbidden() {
        String url = "http://localhost:" + port;

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setName("Тонировка зеркала заднего вида");
        serviceRequest.setSlots(1);

        ResponseEntity<ServiceResponse> response = restTemplate.exchange(url + "/service?token=not-secret-token",
                HttpMethod.PUT, new HttpEntity<>(serviceRequest), ServiceResponse.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void removeServiceReturnTrue() {
        String url = "http://localhost:" + port;

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setName("Тонировка зеркала заднего вида");
        serviceRequest.setSlots(1);

        ResponseEntity<ServiceResponse> response = restTemplate.exchange(url + "/service?token="+ applicationProperties.getToken(),
                HttpMethod.PUT, new HttpEntity<>(serviceRequest), ServiceResponse.class);
        Long id = response.getBody().getId();

        ResponseEntity<ServiceResponse> removeResponse = restTemplate.exchange(url + "/service/" + id,
                HttpMethod.DELETE, new HttpEntity<>(serviceRequest), ServiceResponse.class);
        Assertions.assertEquals(HttpStatus.OK, removeResponse.getStatusCode());

        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT count(*) as count FROM wash_my_car.service where id = " + id, Integer.class);
        Assertions.assertEquals(0, userCount);
    }

    @Test
    void getServiceListReturnTrue() {
        String url = "http://localhost:" + port;

        ResponseEntity<Map> firstResponse = restTemplate.getForEntity(url + "/services", Map.class);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setName("Чистка салона");
        serviceRequest.setSlots(1);

        restTemplate.exchange(url + "/service?token=very-very-secret-token",
                HttpMethod.PUT, new HttpEntity<>(serviceRequest), ServiceResponse.class);

        ResponseEntity<Map> secondResponse = restTemplate.getForEntity(url + "/services", Map.class);

        Assertions.assertEquals(firstResponse.getBody().size() + 1, secondResponse.getBody().size());
    }
}