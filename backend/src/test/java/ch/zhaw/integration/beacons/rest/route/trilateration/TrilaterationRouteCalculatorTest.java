package ch.zhaw.integration.beacons.rest.route.trilateration;

import ch.zhaw.integration.beacons.entities.device.Device;
import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import ch.zhaw.integration.beacons.entities.route.RouteRepository;
import ch.zhaw.integration.beacons.entities.signal.Signal;
import ch.zhaw.integration.beacons.entities.signal.SignalRepository;
import ch.zhaw.integration.beacons.rest.route.trilateration.calculator.PositionCalculator;
import ch.zhaw.integration.beacons.rest.route.trilateration.helper.TrilaterationSignalPreprocessor;
import ch.zhaw.integration.beacons.utils.CalculationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrilaterationRouteCalculatorTest {

    private TrilaterationRouteCalculator sut;

    @Mock
    private  SignalRepository signalRepository;
    @Mock
    private  RouteRepository routeRepository;
    @Mock
    private  TrilaterationSignalPreprocessor trilaterationPreprocessor;
    @Mock
    private  PositionCalculator positionCalculator;

    @BeforeEach
    public void setUp() {
        sut = new TrilaterationRouteCalculator(signalRepository, routeRepository, trilaterationPreprocessor, positionCalculator);
    }

    @Test
    public void calculateRoutesForDeviceTESThappyFlow() {
        // Given
        Device device = mock(Device.class);
        Date routeStartTime = mock(Date.class);
        Date routeEndTime = mock(Date.class);
        Date routeTriggerTime = mock(Date.class);
        List<Signal> rawSignals = List.of(mock(Signal.class), mock(Signal.class), mock(Signal.class));
        given(signalRepository.findAllByDeviceAndSignalTimestampBetween(device, routeStartTime, routeEndTime)).willReturn(rawSignals);
        Map<String, List<Signal>> preprocessedSignals = new HashMap<>();
        preprocessedSignals.put("2022.04.17", rawSignals);
        given(trilaterationPreprocessor.preprocess(rawSignals)).willReturn(preprocessedSignals);
        given(positionCalculator.calculatePositions(any(), any(), any())).willReturn(List.of(mock(Position.class)));
        given(routeRepository.save(any())).willReturn(mock(Route.class));
        // When
        sut.calculateRoutesForDevice(device, routeStartTime, routeEndTime, routeTriggerTime);
        // Then
        verify(signalRepository, times(1)).findAllByDeviceAndSignalTimestampBetween(device, routeStartTime, routeEndTime);
        verify(trilaterationPreprocessor, times(1)).preprocess(rawSignals);
        verify(positionCalculator, times(1)).calculatePositions(eq(CalculationMethod.TRILATERATION_NO_SMOOTHING), eq(preprocessedSignals), any(Route.class));
        verify(positionCalculator, times(1)).calculatePositions(eq(CalculationMethod.TRILATERATION_SLIDING_WINDOW), eq(preprocessedSignals), any(Route.class));
        verify(routeRepository, times(2)).save(any());
    }
}
