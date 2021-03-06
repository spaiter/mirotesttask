package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetCreateRequestDTO;
import com.miro.api.widgets.testtask.entities.WidgetCustomEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MapBasedWidgetEntityRepositoryUnitTest {
    @Test
    public void widgetEntityRepositoryCanCreateWidget() {
        MapBasedWidgetEntityRepository widgetEntityRepository = new MapBasedWidgetEntityRepository();

        WidgetCreateRequestDTO widgetParams = new WidgetCreateRequestDTO(10, 20, 30, 40, 50);
        WidgetCustomEntity widgetEntity = widgetEntityRepository.createEntity(new WidgetCreateDTO(widgetParams));
        assertNotNull(widgetEntity.getId());
    }

    @Test
    public void widgetEntityRepositoryCanSaveWidgetAndGetSameWidgetById() {
        MapBasedWidgetEntityRepository widgetEntityRepository = new MapBasedWidgetEntityRepository();

        long currentTimeStamp = Instant.now().getEpochSecond();
        WidgetCustomEntity widget = new WidgetCustomEntity(10, 20, 30, 40, 50);
        widgetEntityRepository.saveEntity(widget);
        Optional<WidgetCustomEntity> justCreatedWidget = widgetEntityRepository.findEntityById(widget.getId());

        assertEquals(widget.getId(), justCreatedWidget.map(WidgetCustomEntity::getId).orElse("unknown"), "widget id must be the same");
        assertEquals(widget.getXCoordinate(), 10, "widget x-coordinate must be 10");
        assertEquals(widget.getYCoordinate(), 20, "widget y-coordinate must be 20");
        assertEquals(widget.getZIndex(), 30, "widget z-index must be 30");
        assertEquals(widget.getHeight(), 40, "widget height must be 40");
        assertTrue(widget.getUpdatedAt() >= currentTimeStamp, "widget last modified date must be same or greater than now");
    }

    @Test
    public void widgetEntityRepositoryCanShiftWidgetsZIndexes() {
        MapBasedWidgetEntityRepository widgetEntityRepository = new MapBasedWidgetEntityRepository();
        WidgetCustomEntity widget1 = new WidgetCustomEntity(1, 1, 100, 0, 0);
        WidgetCustomEntity widget2 = new WidgetCustomEntity(2, 2, 101, 0, 0);
        WidgetCustomEntity widget3 = new WidgetCustomEntity(3, 3, 110, 0, 0);
        WidgetCustomEntity widget4 = new WidgetCustomEntity(4, 4, 120, 0, 0);
        WidgetCustomEntity widget5 = new WidgetCustomEntity(5, 5, 100, 0, 0);
        WidgetCustomEntity widget6 = new WidgetCustomEntity(6, 6, 100, 0, 0);

        widgetEntityRepository.saveEntity(widget1);
        widgetEntityRepository.saveEntity(widget2);
        widgetEntityRepository.saveEntity(widget3);
        widgetEntityRepository.saveEntity(widget4);
        assertTrue(widgetEntityRepository.isNeedToShift(widget5.getZIndex()));
        widgetEntityRepository.shiftUpwards(widget5.getZIndex());
        widgetEntityRepository.saveEntity(widget5);
        assertTrue(widgetEntityRepository.isNeedToShift(widget6.getZIndex()));
        widgetEntityRepository.shiftUpwards(widget6.getZIndex());
        widgetEntityRepository.saveEntity(widget6);

        List<WidgetCustomEntity> widgets = widgetEntityRepository.findAllEntities();

        assertEquals(6, widgets.size());

        assertEquals(100, widgets.get(0).getZIndex(), "First widget z-index must become 100");
        assertEquals(101, widgets.get(1).getZIndex(), "Second widget z-index must become 101");
        assertEquals(102, widgets.get(2).getZIndex(), "Third widget z-index must become 102");
        assertEquals(103, widgets.get(3).getZIndex(), "Fourth widget z-index must become 103");
        assertEquals(110, widgets.get(4).getZIndex(), "Fifth widget z-index must stay 110");
        assertEquals(120, widgets.get(5).getZIndex(), "Sixth widget z-index must stay 120");

        assertEquals(widget6.getId(), widgets.get(0).getId(), "Sixth widget must stay at first position");
        assertEquals(widget5.getId(), widgets.get(1).getId(), "Fifth widget must shift to second position");
        assertEquals(widget1.getId(), widgets.get(2).getId(), "First widget must shift to third position");
        assertEquals(widget2.getId(), widgets.get(3).getId(), "Second widget must shift to fourth position");
        assertEquals(widget3.getId(), widgets.get(4).getId(), "Third widget must shift to fifth position");
        assertEquals(widget4.getId(), widgets.get(5).getId(), "Fourth widget must shift to sixth position");
    }

    @Test
    public void widgetEntityRepositoryCanDeleteWidgetsById() {
        MapBasedWidgetEntityRepository widgetEntityRepository = new MapBasedWidgetEntityRepository();
        WidgetCustomEntity widget1 = new WidgetCustomEntity(0, 0, 1, 0, 0);
        WidgetCustomEntity widget2 = new WidgetCustomEntity(0, 0, 2, 0, 0);
        WidgetCustomEntity widget3 = new WidgetCustomEntity(0, 0, 3, 0, 0);

        widgetEntityRepository.saveEntity(widget1);
        widgetEntityRepository.saveEntity(widget2);
        widgetEntityRepository.saveEntity(widget3);

        widgetEntityRepository.deleteEntityById(widget1.getId());
        widgetEntityRepository.deleteEntityById(widget3.getId());

        List<WidgetCustomEntity> widgets = widgetEntityRepository.findAllEntities();

        assertEquals(1, widgets.size(), "Widgets count must become 1");
        assertEquals(widget2.getId(), widgets.get(0).getId(), "Last widget must be second widget");
    }
}
