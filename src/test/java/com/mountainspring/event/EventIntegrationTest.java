package com.mountainspring.event;

import com.mountainspring.aws.S3ObjectRepository;
import com.mountainspring.eventMedia.EventMediaRepository;
import com.mountainspring.trip.TripRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventIntegrationTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventService eventService;

    @MockBean
    EventMediaRepository eventMediaRepository;

    @MockBean
    S3ObjectRepository s3ObjectRepository;

    @MockBean
    TripRepository tripRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        List<Event> events = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Event event = new Event();
            event.setName("Event" + i);
            event.setId(UUID.randomUUID());
            events.add(event);
        }
        eventRepository.saveAll(events);
    }

    @Test
    public void should_get_all_events() throws Exception {
        this.mockMvc.perform(get("/events")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].name").value("Event1"))
                .andExpect(jsonPath("$.[1].name").value("Event2"))
                .andExpect(status().isOk());
    }

    @AfterEach
    public void tearDown() {
        eventRepository.deleteAll();
    }
}
