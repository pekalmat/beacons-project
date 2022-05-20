package ch.zhaw.integration.beacons.service.route;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.device.DeviceRepository;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteDto;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.route.RouteToRouteDtoMapper;
import ch.zhaw.integration.beacons.service.route.exporter.RouteDataCsvExporter;
import ch.zhaw.integration.beacons.service.route.trilateration.TrilaterationRouteCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    private RouteService sut;

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private RouteToRouteDtoMapper routeMapper;
    @Mock
    private TrilaterationRouteCalculator trilaterationRouteCalculator;
    @Mock
    private RouteDataCsvExporter routeDataCsvExporter;

    @BeforeEach
    public void setUp() {
        sut = new RouteService(deviceRepository, routeRepository, trilaterationRouteCalculator, routeDataCsvExporter);
        sut.setRouteMapper(routeMapper);
    }

    @Test
    public void calculateRoutesTESThappyFlow() {
        // Given
        Date routeStartTime = mock(Date.class);
        Date routeEndTime = mock(Date.class);
        String type = "point";
        Device device = mock(Device.class);
        given(deviceRepository.findAll()).willReturn(List.of(device));
        Route route = mock(Route.class);
        given(trilaterationRouteCalculator.calculateRoutesForDevice(any(), any(), any(), any())).willReturn(List.of(route));
        RouteDto routeDto = new RouteDto();
        given(routeMapper.mapRouteListToRouteDtoList(List.of(route))).willReturn(List.of(routeDto));
        // When
        List<RouteDto> result = sut.calculateRoutes(routeStartTime, routeEndTime, type);
        // Then
        verify(deviceRepository, times(1)).findAll();
        verify(trilaterationRouteCalculator, times(1)).calculateRoutesForDevice(any(), any(), any(), any());
        verify(routeDataCsvExporter, times(1)).createCsvPerRoute(List.of(route), type);
        assertEquals(1, result.size());
    }

}
