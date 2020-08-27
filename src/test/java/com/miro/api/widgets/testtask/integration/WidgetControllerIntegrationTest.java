package com.miro.api.widgets.testtask.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.api.widgets.testtask.dto.WidgetCreateRequestDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class WidgetControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapBasedWidgetEntityRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    private void purgeRepo() {
        repository.purge();
    }

    @Test
    public void whenPostRequestToWidgetsAndValidWidget_thenCorrectResponse() throws Exception {
        WidgetCreateRequestDTO request = new WidgetCreateRequestDTO(10, 20, 30, 40, 50);
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String actualResponseBody = result.getResponse().getContentAsString();
        WidgetResponseDTO responseDTO = objectMapper.readValue(actualResponseBody, WidgetResponseDTO.class);

        Optional<WidgetEntity> widget = repository.findEntityById(responseDTO.getId());

        assertTrue(widget.isPresent());
        assertThat(widget.get().getXCoordinate()).isEqualTo(10);
        assertThat(widget.get().getYCoordinate()).isEqualTo(20);
        assertThat(widget.get().getZIndex()).isEqualTo(30);
        assertThat(widget.get().getHeight()).isEqualTo(40);
        assertThat(widget.get().getWidth()).isEqualTo(50);
    }

    @Test
    public void whenManyPostRequestToWidgetsAndValidWidgets_thenCorrectResponse() throws Exception {
        WidgetCreateRequestDTO request = new WidgetCreateRequestDTO(10, 20, 30, 40, 50);
        String jsonRequest = objectMapper.writeValueAsString(request);

        int requestsCount = 10;
        do {
            requestsCount -= 1;
            mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

        } while (requestsCount != 0);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String actualResponseBody = result.getResponse().getContentAsString();
        WidgetResponseDTO[] responseDTO = objectMapper.readValue(actualResponseBody, WidgetResponseDTO[].class);

        assertThat(responseDTO.length).isEqualTo(10);
        AtomicInteger zIndex = new AtomicInteger(30);
        //noinspection ResultOfMethodCallIgnored
        Arrays.stream(responseDTO).map(widgetResponse -> {
            assertThat(widgetResponse.getZIndex()).isEqualTo(zIndex.get());
            zIndex.addAndGet(1);
            return null;
        });
    }

    @Test
    public void whenManyPostRequestToWidgetsAndValidWidgetsWithDifferentZIndex_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = List.of(
                new WidgetCreateRequestDTO(0, 0, 10, 1, 1),
                new WidgetCreateRequestDTO(1, 1, 20, 2, 2),
                new WidgetCreateRequestDTO(2, 2, 30, 3, 3),
                new WidgetCreateRequestDTO(3, 3, 20, 4, 4),
                new WidgetCreateRequestDTO(4, 4, 4, 5, 5),
                new WidgetCreateRequestDTO(5, 5, 4, 6, 6)
        ).stream().map(request -> {
            try {
                return objectMapper.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).map(json -> {
            try {
                return mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).map(response -> {
            try {
                return response.getResponse().getContentAsString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).map(result -> {
            try {
                return objectMapper.readValue(result, WidgetResponseDTO.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseBody = result.getResponse().getContentAsString();
        WidgetResponseDTO[] responseDTO = objectMapper.readValue(actualResponseBody, WidgetResponseDTO[].class);
        List<WidgetResponseDTO> widgets = Arrays.asList(responseDTO);

        assertThat(widgets.size()).isEqualTo(requests.size());

        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(5).getId());
        assertThat(widgets.get(1).getId()).isEqualTo(requests.get(4).getId());
        assertThat(widgets.get(2).getId()).isEqualTo(requests.get(0).getId());
        assertThat(widgets.get(3).getId()).isEqualTo(requests.get(3).getId());
        assertThat(widgets.get(4).getId()).isEqualTo(requests.get(1).getId());
        assertThat(widgets.get(5).getId()).isEqualTo(requests.get(2).getId());

        assertThat(widgets.get(0).getZIndex()).isEqualTo(4);
        assertThat(widgets.get(1).getZIndex()).isEqualTo(5);
        assertThat(widgets.get(2).getZIndex()).isEqualTo(10);
        assertThat(widgets.get(3).getZIndex()).isEqualTo(20);
        assertThat(widgets.get(4).getZIndex()).isEqualTo(21);
        assertThat(widgets.get(5).getZIndex()).isEqualTo(30);
    }

}