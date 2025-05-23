package be.dash.dashserver.api.core.external;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.external.dto.LocationsResponse;
import be.dash.dashserver.api.support.Permission;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.external.LocationSearchService;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class LocationSearchController {
    private final LocationSearchService locationSearchService;

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @GetMapping("/locations")
    public ResponseEntity<LocationsResponse> getLocations(@RequestParam @NotBlank final String query) {
        return ResponseEntity.ok(LocationsResponse.of(locationSearchService.getLocations(query)));
    }
}
