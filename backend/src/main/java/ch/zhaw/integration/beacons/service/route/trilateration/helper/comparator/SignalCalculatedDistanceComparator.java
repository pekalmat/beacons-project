package ch.zhaw.integration.beacons.service.route.trilateration.helper.comparator;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SignalCalculatedDistanceComparator implements Comparator<Signal> {

    @Override
    public int compare(Signal o1, Signal o2) {
        return o1.getCalculatedDistance().compareTo(o2.getCalculatedDistance());
    }
}
