package com.mountainspring.vector;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class EventVectorController {

    private final EmbeddingModel embeddingModel;
    private final EventVectorService eventVectorService;

    @Autowired
    public EventVectorController(EmbeddingModel embeddingModel, EventVectorService eventVectorService) {
        this.embeddingModel = embeddingModel;
        this.eventVectorService = eventVectorService;
    }

    @PostMapping("/embedding")
    public EmbeddingResponse embed(@RequestBody Map<String, String> map) {
        String text = map.get("text");
        String eventId = map.get("eventId");
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(text));
        eventVectorService.createOne(eventId, embeddingResponse.getResult().getOutput());
        return embeddingResponse;
    }

    @PostMapping("/embedding/generateAll")
    public ResponseEntity<?> embed() {
        eventVectorService.generateAll();
        return new ResponseEntity<>(
                "ok",
                HttpStatus.OK
        );
    }

    @PostMapping("/embedding/search")
    public List<Map<String, Object>> search(@RequestBody Map<String, String> map) {
        String text = map.get("text");
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(text));

        List<Map<String, Object>> res = eventVectorService.search(embeddingResponse.getResult().getOutput());
        return res;
    }

}