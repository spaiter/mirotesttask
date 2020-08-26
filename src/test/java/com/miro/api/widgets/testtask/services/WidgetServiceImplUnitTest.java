package com.miro.api.widgets.testtask.services;


import com.miro.api.widgets.testtask.entities.WidgetCreateParamsHelperEntity;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class WidgetServiceImplUnitTest {

    @Mock
    private MapBasedWidgetEntityRepository widgetRepository;

    @InjectMocks
    private WidgetServiceImpl widgetService;

    @Test
    public void whenCreateAndSaveWidgetByConstructorParams_thenReturnWidgetEntity() {
        final WidgetCreateParamsHelperEntity widgetParams = new WidgetCreateParamsHelperEntity(10, 20, 30, 40, 50);
        final WidgetEntity widget = new WidgetEntity(10, 20, 30, 40, 50);
        given(widgetRepository.isNeedToShift(30)).willReturn(false);
        given(widgetRepository.createEntity(widgetParams)).willReturn(widget);
        given(widgetRepository.saveEntity(widget)).willReturn(widget);

        WidgetEntity createdWidget = widgetService.createAndSaveWidget(widgetParams);

        assertThat(createdWidget).isEqualTo(widget);
    }

    @Test
    public void whenCreateAndSaveWidgetByConstructorParamsWithNullZIndex_thenReturnWidgetEntityWithTopZIndex() {
        final WidgetCreateParamsHelperEntity widgetParams = new WidgetCreateParamsHelperEntity(10, 20, 30, 40, 50);
        final WidgetEntity widget = new WidgetEntity(10, 20, 30, 40, 50);
        final WidgetEntity prevWidgetWithSameZIndex = new WidgetEntity(10, 20, 31, 40, 50);

        given(widgetRepository.isNeedToShift(30)).willReturn(true);
        given(widgetRepository.createEntity(widgetParams)).willReturn(widget);
        given(widgetRepository.saveEntity(widget)).willReturn(widget);

        WidgetEntity createdWidget = widgetService.createAndSaveWidget(widgetParams);

        assertThat(createdWidget).isEqualTo(widget);
        assertThat(prevWidgetWithSameZIndex.getZIndex()).isEqualTo(31);
    }

    @Test
    public void whenCreateAndSaveWidgetByConstructorParamsWithNegativeWidth_thenThrowIllegalArgumentException() {
        final WidgetCreateParamsHelperEntity widgetParams = new WidgetCreateParamsHelperEntity(10, 20, 30, 40, -50);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> widgetService.createAndSaveWidget(widgetParams));

        String expectedMessage = "Widget width can't be negative.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void whenCreateAndSaveWidgetByConstructorParamsWithNegativeHeight_thenThrowIllegalArgumentException() {
        final WidgetCreateParamsHelperEntity widgetParams = new WidgetCreateParamsHelperEntity(10, 20, 30, -40, 50);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> widgetService.createAndSaveWidget(widgetParams));

        String expectedMessage = "Widget height can't be negative.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void whenGetWidgetByExistingId_thenReturnOptionalWithWidgetEntity() {
        final WidgetEntity widget = new WidgetEntity(10, 20, 30, 40, 50);
        given(widgetRepository.findEntityById(widget.getId())).willReturn(Optional.of(widget));

        Optional<WidgetEntity> foundWidget = widgetService.getWidgetById(widget.getId());

        //noinspection OptionalGetWithoutIsPresent
        assertThat(foundWidget.get()).isEqualTo(widget);
    }

    @Test
    void whenGetWidgetByNotExistingId_thenReturnEmptyOptional() {
        final String uniqueId = "unique_id";
        given(widgetRepository.findEntityById(uniqueId)).willReturn(Optional.empty());

        Optional<WidgetEntity> foundWidget = widgetService.getWidgetById(uniqueId);

        assertTrue(foundWidget.isEmpty());
    }

    @Test
    void whenDeleteExistingWidgetById_thenReturnTrue() {
        final String uniqueId = "unique_id";
        given(widgetRepository.deleteEntityById(uniqueId)).willReturn(true);

        boolean deleteResult = widgetService.deleteWidgetById(uniqueId);

        assertTrue(deleteResult);
    }

    @Test
    void whenDeleteNotExistingWidgetById_thenReturnFalse() {
        final String uniqueId = "unique_id";
        given(widgetRepository.deleteEntityById(uniqueId)).willReturn(false);

        boolean deleteResult = widgetService.deleteWidgetById(uniqueId);

        assertFalse(deleteResult);
    }

    @Test
    void whenGetAllWidgets_thenReturnWidgetsListOrderedAscByZIndex() {
        final WidgetEntity widget1 = new WidgetEntity(1, 1, 1, 1, 1);
        final WidgetEntity widget2 = new WidgetEntity(2, 2, 2, 2, 2);
        final WidgetEntity widget3 = new WidgetEntity(3, 3, 3, 3, 3);

        List<WidgetEntity> widgets = List.of(widget1, widget2, widget3);

        given(widgetRepository.findAllEntities()).willReturn(widgets);

        List<WidgetEntity> foundWidgets = widgetService.getAllWidgets();

        assertThat(widgets).isEqualTo(foundWidgets);
    }
}