package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.ContentStatusUpdateDto;
import com.gallery.fineart.mfineart.dto.EventDto;
import com.gallery.fineart.mfineart.dto.EventUploadDto;
import com.gallery.fineart.mfineart.service.event.EventService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/events")
public class EventController {
    
    private final EventService eventService;
    
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents(
            @RequestParam(defaultValue = "true") boolean sorted) {
        List<EventDto> events = eventService.getAllEvents(sorted);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable String id) {
        EventDto eventDto = eventService.getEventById(id);

        return ResponseEntity.ok(eventDto);
    }

    @PostMapping("/event")
    public ResponseEntity<Long> addEvent(@Valid @RequestBody EventDto eventDto) {
        Long eventId = eventService.addEvent(eventDto).getId();

        return ResponseEntity.ok(eventId);
    }

    @PostMapping("/event-images")
    public ResponseEntity<Long> addEventWithImages(@Valid @RequestBody EventUploadDto dto) {
        Long eventId = eventService.addEvent(dto.getEventDto(), dto.getImagesFiles()).getId();

        return ResponseEntity.ok(eventId);
    }

    @PutMapping("/event/content-status")
    public ResponseEntity<ContentStatusUpdateDto> updateContentStatus(
            @Valid @RequestBody ContentStatusUpdateDto contentStatusUpdateDto) {
        ContentStatusUpdateDto result = eventService.updateContentStatus(contentStatusUpdateDto);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/event")
    public ResponseEntity<Long> editEvent(@Valid @RequestBody EventDto eventDto) {
        Long eventId = eventService.editEvent(eventDto);

        return ResponseEntity.ok(eventId);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEventById(@RequestParam String eventId) {
        Boolean isDeleted = eventService.deleteEventById(eventId);

        JSONObject result = new JSONObject().put("deleted", isDeleted);

        return ResponseEntity.ok(result.toString());
    }
}
