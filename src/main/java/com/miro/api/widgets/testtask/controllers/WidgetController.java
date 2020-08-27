package com.miro.api.widgets.testtask.controllers;

import com.miro.api.widgets.testtask.dto.*;
import com.miro.api.widgets.testtask.services.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/widgets")
public class WidgetController {
    private final WidgetService<WidgetResponseDTO> widgetService;

    public WidgetController(WidgetService<WidgetResponseDTO> widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping
    public ResponseEntity<List<WidgetResponseDTO>> getAllWidgets() {
        return new ResponseEntity<>(widgetService.getAllWidgets(), HttpStatus.OK);
    }

    @GetMapping(value = "/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidgetById(@PathVariable String widgetId) {
        return ResponseEntity.of(widgetService.getWidgetById(widgetId));
    }

    @PostMapping
    public ResponseEntity<WidgetResponseDTO> createWidget(@Valid @RequestBody WidgetCreateRequestDTO createRequestDTO) {
        WidgetCreateDTO widgetCreateDTO = new WidgetCreateDTO(createRequestDTO);
        return new ResponseEntity<>(widgetService.createAndSaveWidget(widgetCreateDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> updateWidget(@PathVariable String widgetId, @Valid @RequestBody WidgetUpdateRequestDTO updateRequestDTO) {
        WidgetUpdateDTO widgetUpdateDTO = new WidgetUpdateDTO(updateRequestDTO);
        return ResponseEntity.of(widgetService.updateWidgetById(widgetId, widgetUpdateDTO));
    }

    @DeleteMapping(value = "/{widgetId}")
    public ResponseEntity<Void> deleteWidgetById(@PathVariable String widgetId) {
        boolean deleteResult = widgetService.deleteWidgetById(widgetId);
        return new ResponseEntity<>(deleteResult ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
