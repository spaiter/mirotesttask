package com.miro.api.widgets.testtask.controllers;

import com.miro.api.widgets.testtask.dto.*;
import com.miro.api.widgets.testtask.services.WidgetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/widgets")
@Validated
public class WidgetController {
    private final WidgetService<WidgetResponseDTO> widgetService;

    public WidgetController(WidgetService<WidgetResponseDTO> widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping
    public ResponseEntity<List<WidgetResponseDTO>> getAllWidgets() {
        return new ResponseEntity<>(widgetService.getAllWidgets(), HttpStatus.OK);
    }

    @GetMapping(params = { "page", "size" })
    public ResponseEntity<Page<WidgetResponseDTO>> getWidgets(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(500) Integer size
    ) {
        Pageable pageRequest = PageRequest.of(page, size);
        return new ResponseEntity<>(widgetService.getAllWidgets(pageRequest), HttpStatus.OK);
    }

    @GetMapping(params = { "page", "size", "x1", "y1", "x2", "y2" })
    public ResponseEntity<Page<WidgetResponseDTO>> getFilteredWidgets(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(500) Integer size,
            @RequestParam(value = "x1") @NotNull Integer x1,
            @RequestParam(value = "y1") @NotNull Integer y1,
            @RequestParam(value = "x2") @NotNull Integer x2,
            @RequestParam(value = "y2") @NotNull Integer y2
    ) {
        Pageable pageRequest = PageRequest.of(page, size);
        WidgetFilterDTO filter = new WidgetFilterDTO(x1, y1, x2, y2);
        return new ResponseEntity<>(widgetService.getFilteredWidgets(pageRequest, filter), HttpStatus.OK);
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
