package be.dash.dashserver.api.core.advertisement;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import be.dash.dashserver.core.auth.JwtTokenExtractor;
import be.dash.dashserver.core.auth.TokenParser;
import be.dash.dashserver.core.domain.advertisement.Advertisement;
import be.dash.dashserver.core.domain.advertisement.service.AdvertisementService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdvertisementController.class)
class AdvertisementControllerTest {

    @MockitoBean
    private AdvertisementService advertisementService;
    @MockitoBean
    private JwtTokenExtractor jwtTokenExtractor;
    @MockitoBean
    private TokenParser tokenParser;
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("광고 목록을 반환한다.")
    @Test
    void advertisement() throws Exception {
        Advertisement advertisement = new Advertisement(1L, "https://example.com/image1.jpg", "설명 1");
        Advertisement advertisement1 = new Advertisement(2L, "https://example.com/image2.jpg", "설명 2");
        when(advertisementService.getAdvertisement()).thenReturn(List.of(advertisement, advertisement1));

        mockMvc.perform(get("/api/v1/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advertisements[0].id").value(advertisement.id()))
                .andExpect(jsonPath("$.advertisements[0].imageUrl").value(advertisement.imageUrl()))
                .andExpect(jsonPath("$.advertisements[0].description").value(advertisement.description()))
                .andExpect(jsonPath("$.advertisements[1].id").value(advertisement1.id()))
                .andExpect(jsonPath("$.advertisements[1].imageUrl").value(advertisement1.imageUrl()))
                .andExpect(jsonPath("$.advertisements[1].description").value(advertisement1.description()));
    }

    @DisplayName("특정 ID를 가진 광고를 조회한다.")
    @Test
    void findById() throws Exception {
        Long advertisementId = 1L;
        Advertisement advertisement = new Advertisement(advertisementId, "https://example.com/image1.jpg", "설명 1");
        when(advertisementService.findById(advertisementId)).thenReturn(advertisement);

        mockMvc.perform(get("/api/v1/advertisements/{id}", advertisementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(advertisement.id()))
                .andExpect(jsonPath("$.imageUrl").value(advertisement.imageUrl()))
                .andExpect(jsonPath("$.description").value(advertisement.description()));
    }

    @DisplayName("존재하지 않는 광고 ID로 조회하면 404를 반환한다.")
    @Test
    void findById_NotFound() throws Exception {
        Long advertisementId = 999L;
        when(advertisementService.findById(advertisementId))
                .thenThrow(new be.dash.dashserver.core.exception.NotFoundException("광고를 찾을 수 없습니다."));

        mockMvc.perform(get("/api/v1/advertisements/{id}", advertisementId))
                .andExpect(status().isNotFound());
    }

    @DisplayName("잘못된 형식의 광고 ID로 조회하면 400을 반환한다.")
    @Test
    void findById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/advertisements/{id}", 0))
                .andExpect(status().isBadRequest());
    }
}
