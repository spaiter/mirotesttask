package com.miro.api.widgets.testtask.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.api.widgets.testtask.dto.WidgetCreateRequestDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateRequestDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.exceptions.ErrorResponse;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.utils.PageObjectMapperModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WidgetControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapBasedWidgetEntityRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    @BeforeEach
    private void purgeRepo() {
        repository.purge();
    }

    @BeforeAll
    private void addObjectMapperModules() {
        objectMapper.registerModule(new PageObjectMapperModule());
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
    public void whenPostRequestsToWidgetsWithNegativeZIndex_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = List.of(
                new WidgetCreateRequestDTO(0, 0, -10, 1, 1),
                new WidgetCreateRequestDTO(1, 1, -20, 2, 2),
                new WidgetCreateRequestDTO(1, 1, -20, 2, 2),
                new WidgetCreateRequestDTO(2, 2, 30, 3, 3)
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

        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(2).getId());
        assertThat(widgets.get(0).getZIndex()).isEqualTo(-20);
        assertThat(widgets.get(1).getId()).isEqualTo(requests.get(1).getId());
        assertThat(widgets.get(1).getZIndex()).isEqualTo(-19);
        assertThat(widgets.get(2).getId()).isEqualTo(requests.get(0).getId());
        assertThat(widgets.get(3).getId()).isEqualTo(requests.get(3).getId());
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


    @Test
    public void whenGetManyByPage_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = IntStream
                .range(1, 1001)
                .mapToObj(i -> new WidgetCreateRequestDTO(i, i, i, i, i))
                .map(request -> {
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
                .queryParam("page", "3")
                .queryParam("size", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseBody = result.getResponse().getContentAsString();
        Page<WidgetResponseDTO> responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        List<WidgetResponseDTO> widgets = responseDTO.getContent();

        assertThat(widgets.size()).isEqualTo(100);
        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(300).getId());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .queryParam("page", "0")
                .queryParam("size", "500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        actualResponseBody = result.getResponse().getContentAsString();
        responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        widgets = responseDTO.getContent();

        assertThat(widgets.size()).isEqualTo(500);
        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(0).getId());
        assertThat(widgets.get(499).getId()).isEqualTo(requests.get(499).getId());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .queryParam("page", "-1")
                .queryParam("size", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        actualResponseBody = result.getResponse().getContentAsString();
        ErrorResponse validationError = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        validationError.getDetails().sort(String::compareTo);

        assertThat(validationError.getMessage()).isEqualTo("Invalid query params.");
        assertThat(validationError.getDetails().size()).isEqualTo(2);

        assertThat(validationError.getDetails().get(0)).isEqualTo("page - must be greater than or equal to 0");
        assertThat(validationError.getDetails().get(1)).isEqualTo("size - must be less than or equal to 500");
    }

    @Test
    public void whenGetFilteredRequests_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = List.of(
                new WidgetCreateRequestDTO(-3, -4, 1, 3, 2),
                new WidgetCreateRequestDTO(-2, 1, 2, 1, 1),
                new WidgetCreateRequestDTO(-3, 0, 3, 5, 3),
                new WidgetCreateRequestDTO(1, 1, 4, 2, 2),
                new WidgetCreateRequestDTO(2, -1, 5, 2, 3)
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
                .queryParam("page", "0")
                .queryParam("size", "10")
                .queryParam("x1", "-4")
                .queryParam("y1", "-4")
                .queryParam("x2", "3")
                .queryParam("y2", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseBody = result.getResponse().getContentAsString();
        Page<WidgetResponseDTO> responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        List<WidgetResponseDTO> widgets = responseDTO.getContent();

        assertThat(widgets.size()).isEqualTo(3);

        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(0).getId());
        assertThat(widgets.get(1).getId()).isEqualTo(requests.get(1).getId());
        assertThat(widgets.get(2).getId()).isEqualTo(requests.get(3).getId());
    }

    @Test
    public void whenGetFilteredRequestsAfterUpdateRequest_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = List.of(
                new WidgetCreateRequestDTO(-3, -4, -5, 3, 2),
                new WidgetCreateRequestDTO(-2, 1, 2, 1, 1),
                new WidgetCreateRequestDTO(-3, 0, 3, 5, 3),
                new WidgetCreateRequestDTO(1, 1, 4, 2, 2),
                new WidgetCreateRequestDTO(2, -1, 5, 2, 3)
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

        WidgetResponseDTO widget1 = requests.get(0);

        WidgetUpdateRequestDTO widgetUpdateRequestDTO = new WidgetUpdateRequestDTO(
                widget1.getXCoordinate(),
                widget1.getYCoordinate(),
                widget1.getZIndex(),
                10,
                widget1.getWidth()
        );
        String jsonUpdateRequest = objectMapper.writeValueAsString(widgetUpdateRequestDTO);

        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/widgets/%s", widget1.getId()))
                .content(jsonUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .queryParam("page", "0")
                .queryParam("size", "10")
                .queryParam("x1", "-4")
                .queryParam("y1", "-4")
                .queryParam("x2", "3")
                .queryParam("y2", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseBody = result.getResponse().getContentAsString();
        Page<WidgetResponseDTO> responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        List<WidgetResponseDTO> widgets = responseDTO.getContent();

        assertThat(widgets.size()).isEqualTo(2);

        assertThat(widgets.get(0).getId()).isEqualTo(requests.get(1).getId());
        assertThat(widgets.get(1).getId()).isEqualTo(requests.get(3).getId());
    }


    @Test
    public void whenGetFilteredRequestsAfterWidgetsShiftingRequest_thenCorrectResponse() throws Exception {
        List<WidgetResponseDTO> requests = List.of(
                new WidgetCreateRequestDTO(-3, -4, 1, 3, 2),
                new WidgetCreateRequestDTO(-2, 1, 10, 1, 1),
                new WidgetCreateRequestDTO(-3, 0, 20, 5, 3),
                new WidgetCreateRequestDTO(1, 1, 30, 2, 2),
                new WidgetCreateRequestDTO(2, -1, 40, 2, 3),
                new WidgetCreateRequestDTO(-1, -1, 1, 2, 2)
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
                .queryParam("page", "0")
                .queryParam("size", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseBody = result.getResponse().getContentAsString();
        Page<WidgetResponseDTO> responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        List<WidgetResponseDTO> allWidgets = responseDTO.getContent();

        assertThat(allWidgets.size()).isEqualTo(6);

        result = mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .queryParam("page", "0")
                .queryParam("size", "10")
                .queryParam("x1", "-4")
                .queryParam("y1", "-4")
                .queryParam("x2", "3")
                .queryParam("y2", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        actualResponseBody = result.getResponse().getContentAsString();
        responseDTO = objectMapper.readValue(actualResponseBody, new TypeReference<>() {});
        List<WidgetResponseDTO> filteredWidgets = responseDTO.getContent();

        assertThat(filteredWidgets.size()).isEqualTo(4);

        assertThat(filteredWidgets.get(0).getId()).isEqualTo(allWidgets.get(0).getId());
        assertThat(filteredWidgets.get(1).getId()).isEqualTo(allWidgets.get(1).getId());
        assertThat(filteredWidgets.get(2).getId()).isEqualTo(allWidgets.get(2).getId());
        assertThat(filteredWidgets.get(3).getId()).isEqualTo(allWidgets.get(4).getId());
    }

}