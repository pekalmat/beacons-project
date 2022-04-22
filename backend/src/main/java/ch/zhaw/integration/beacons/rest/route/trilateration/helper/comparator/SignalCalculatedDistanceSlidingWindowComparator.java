package ch.zhaw.integration.beacons.rest.route.trilateration.helper.comparator;

import ch.zhaw.integration.beacons.entities.signal.Signal;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SignalCalculatedDistanceSlidingWindowComparator implements Comparator<Signal> {

    @Override
    public int compare(Signal o1, Signal o2) {
        return o1.getCalculatedDistanceSlidingWindow().compareTo(o2.getCalculatedDistanceSlidingWindow());
    }

}
