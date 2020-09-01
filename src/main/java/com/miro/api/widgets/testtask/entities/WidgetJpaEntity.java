package com.miro.api.widgets.testtask.entities;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity(name="widget")
@Table(name="widgets",
    indexes = {
            @Index(name = "filtering_index", columnList = "x1_coordinate,y1_coordinate,x2_coordinate,y2_coordinate"),
            @Index(name = "unique_index__z_index", columnList = "z_index")
    }
)
public class WidgetJpaEntity extends AbstractWidgetEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @Column(name = "x1_coordinate", nullable = false)
    private Integer x1Coordinate;

    @Column(name = "y1_coordinate", nullable = false)
    private Integer y1Coordinate;

    @Transient
    private Integer width;

    @Transient
    private Integer height;

    @Column(name = "x2_coordinate", nullable = false)
    private Integer x2Coordinate;

    @Column(name = "y2_coordinate", nullable = false)
    private Integer y2Coordinate;

    @Column(name = "z_index", nullable = false)
    private Integer zIndex;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt = Instant.now().getEpochSecond();

    public WidgetJpaEntity(WidgetCreateDTO createDTO) {
        this.id = UUID.randomUUID().toString();
        this.x1Coordinate = createDTO.getXCoordinate();
        this.y1Coordinate = createDTO.getYCoordinate();
        this.zIndex = createDTO.getZIndex();
        this.x2Coordinate = createDTO.getXCoordinate() + createDTO.getWidth();
        this.y2Coordinate = createDTO.getYCoordinate() + createDTO.getHeight();
        this.updatedAt = Instant.now().getEpochSecond();
    }

    public WidgetJpaEntity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getXCoordinate() {
        return this.x1Coordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.x1Coordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return this.y1Coordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.y1Coordinate = yCoordinate;
    }

    public int getWidth() {
        return this.x2Coordinate - this.x1Coordinate;
    }

    public void setWidth(int width) {
        this.width = this.x2Coordinate = this.x1Coordinate + width;
    }

    public int getHeight() {
        return this.y2Coordinate - this.y1Coordinate;
    }

    public void setHeight(int height) {
        this.height = this.y2Coordinate = this.y1Coordinate + height;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
