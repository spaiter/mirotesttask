package com.miro.api.widgets.testtask.controllers;

import com.miro.api.widgets.testtask.dto.WidgetCreateRequestDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateRequestDTO;
import com.miro.api.widgets.testtask.entities.WidgetCreateParamsHelperEntity;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.entities.WidgetUpdateParamsHelperEntity;
import com.miro.api.widgets.testtask.services.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/widgets")
public class WidgetController {
    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    private WidgetCreateParamsHelperEntity convertWidgetCreateRequestDtoToWidgetCreateParamsHelperEntity(WidgetCreateRequestDTO createRequestDTO) {
        return new WidgetCreateParamsHelperEntity(
                createRequestDTO.getXCoordinate(),
                createRequestDTO.getYCoordinate(),
                createRequestDTO.getZIndex(),
                createRequestDTO.getHeight(),
                createRequestDTO.getWidth()
        );
    }

    private WidgetResponseDTO convertWidgetEntityToWidgetResponseDTO(WidgetEntity widgetEntity) {
        return new WidgetResponseDTO(
                widgetEntity.getId(),
                widgetEntity.getXCoordinate(),
                widgetEntity.getYCoordinate(),
                widgetEntity.getZIndex(),
                widgetEntity.getHeight(),
                widgetEntity.getHeight(),
                widgetEntity.getUpdatedAt()
        );
    }

    private WidgetUpdateParamsHelperEntity convertWidgetUpdateRequestDtoToWidgetUpdateParamsHelperEntity(WidgetUpdateRequestDTO updateRequestDTO) {
        return new WidgetUpdateParamsHelperEntity(
                updateRequestDTO.getXCoordinate(),
                updateRequestDTO.getYCoordinate(),
                updateRequestDTO.getZIndex(),
                updateRequestDTO.getHeight(),
                updateRequestDTO.getWidth()
        );
    }

    @GetMapping
    public ResponseEntity<List<WidgetResponseDTO>> getAllWidgets() {
        List<WidgetEntity> widgets = widgetService.getAllWidgets();
        List<WidgetResponseDTO> response = widgets.stream().map(this::convertWidgetEntityToWidgetResponseDTO).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidgetById(@PathVariable String widgetId) {
        Optional<WidgetEntity> widget = widgetService.getWidgetById(widgetId);
        return ResponseEntity.of(widget.map(this::convertWidgetEntityToWidgetResponseDTO));
    }

    @PostMapping
    public ResponseEntity<WidgetResponseDTO> createWidget(@Valid @RequestBody WidgetCreateRequestDTO createRequestDTO) {
        WidgetCreateParamsHelperEntity createParamsHelperEntity = convertWidgetCreateRequestDtoToWidgetCreateParamsHelperEntity(createRequestDTO);
        WidgetEntity widgetEntity = widgetService.createAndSaveWidget(createParamsHelperEntity);
        WidgetResponseDTO response = convertWidgetEntityToWidgetResponseDTO(widgetEntity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> updateWidget(@PathVariable String widgetId, @Valid @RequestBody WidgetUpdateRequestDTO updateRequestDTO) {
        WidgetUpdateParamsHelperEntity updateParamsHelperEntity = convertWidgetUpdateRequestDtoToWidgetUpdateParamsHelperEntity(updateRequestDTO);
        Optional<WidgetEntity> widgetEntity = widgetService.updateWidgetById(widgetId, updateParamsHelperEntity);
        return ResponseEntity.of(widgetEntity.map(this::convertWidgetEntityToWidgetResponseDTO));
    }

    @DeleteMapping(value = "/{widgetId}")
    public ResponseEntity<Void> deleteWidgetById(@PathVariable String widgetId) {
        boolean deleteResult = widgetService.deleteWidgetById(widgetId);
        return new ResponseEntity<>(deleteResult ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
