package com.mountainspring.vector;

import com.mountainspring.event.Event;
import com.mountainspring.event.EventRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EventVectorService {

    private final EventVectorRepository eventVectorRepository;

    private final EventRepository eventRepository;

    private final EmbeddingModel embeddingModel;


    public EventVectorService(
            EventVectorRepository eventVectorRepository,
            EventRepository eventRepository,
            EmbeddingModel embeddingModel
    ) {
        this.eventVectorRepository = eventVectorRepository;
        this.eventRepository = eventRepository;
        this.embeddingModel = embeddingModel;
    }

    public void createOne(String uuid, List<Double> embedding) {
        Event event = new Event();
        event.setId(UUID.fromString(uuid));
        EventVector eventVector = new EventVector();
        eventVector.setEvent(event);
        eventVector.setEmbedding(embedding);
        eventVectorRepository.save(eventVector);
    }

    public void getEmbeddingAndCreate(String uuid, String text) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(text));
        createOne(uuid, embeddingResponse.getResult().getOutput());
    }

    public void generateAll() {
        List<Event> events = eventRepository.findAll();
        for (Event event : events) {
            if (event.getDescription() != null && event.getDescriptionId() != null) {
                getEmbeddingAndCreate(String.valueOf(event.getId()), event.getDescriptionId() + " " + event.getDescription());
            }
        }
    }

    public List<Map<String, Object>> search(List<Double> embedding) {
        return eventVectorRepository.search(embedding.toString());
    }


}
