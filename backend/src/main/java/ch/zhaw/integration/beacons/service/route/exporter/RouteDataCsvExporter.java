package ch.zhaw.integration.beacons.service.route.exporter;

import ch.zhaw.integration.beacons.entities.position.Position;
import ch.zhaw.integration.beacons.entities.route.Route;
import liquibase.repackaged.org.apache.commons.collections4.ListUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RouteDataCsvExporter {

    private static final String OUT_FOLDER = "out//routes//";
    private static final String CSV_FILE = ".csv";

    private static final String SEMICOLON_COLUMN_DELIMITER = ";";

    private static final String CALCULATION_METHOD = "Calculation-Method";
    private static final String CALC_TRIGGER_TIME = "Calculation Time";
    private static final String ROUTE_START_TIME = "Route Start-Time";
    private static final String ROUTE_END_TIME = "Rout End-time";
    private static final String DEVICE_FINGERPRINT = "Device-Fingerprint";
    private static final String DEVICE_ID = "Device-ID";
    private static final String GEO_SHAPE = "Geo Shape";
    private static final String POSITION_TIMESTAMP = "Position Time";
    private static final String POSITION_FLOOR_EST = "Estimated Floor";
    private static final String POSITION_SIGNALS_FLOORS = "signals_floors";
    private static final String SIGNAL_1_ID = "Signal 1 ID";
    private static final String SIGNAL_1_BEACON_ID = "Signal 1 Beacon-ID";
    private static final String SIGNAL_2_ID = "Signal 2 ID";
    private static final String SIGNAL_2_BEACON_ID = "Signal 2 Beacon-ID";
    private static final String SIGNAL_3_ID = "Signal 3 ID";
    private static final String SIGNAL_3_BEACON_ID = "Signal 3 Beacon-ID";
    private static final String[] HEADERS_LINES = {
            CALCULATION_METHOD, CALC_TRIGGER_TIME, ROUTE_START_TIME, ROUTE_END_TIME, DEVICE_FINGERPRINT, DEVICE_ID, GEO_SHAPE, POSITION_TIMESTAMP, POSITION_FLOOR_EST, POSITION_SIGNALS_FLOORS,
            SIGNAL_1_ID, SIGNAL_1_BEACON_ID, SIGNAL_2_ID, SIGNAL_2_BEACON_ID, SIGNAL_3_ID, SIGNAL_3_BEACON_ID
    };
    private static final String GEOPOS = "geopos";
    private static final String GEOPOSITION = "Geoposition";
    private static final String[] HEADERS_POINTS = {
            CALCULATION_METHOD, CALC_TRIGGER_TIME, ROUTE_START_TIME, ROUTE_END_TIME, DEVICE_FINGERPRINT, DEVICE_ID, GEOPOS, GEOPOSITION, POSITION_TIMESTAMP, POSITION_FLOOR_EST, POSITION_SIGNALS_FLOORS,
            SIGNAL_1_ID, SIGNAL_1_BEACON_ID, SIGNAL_2_ID, SIGNAL_2_BEACON_ID, SIGNAL_3_ID, SIGNAL_3_BEACON_ID
    };

    public void createCsvPerRoute(List<Route> deviceRoutes, String type) {
        for(Route route : deviceRoutes) {
            exportRouteToCsv(route, type);
        }
    }

    private void exportRouteToCsv(Route route, String type) {
        String fileName = getOutFileName(route, type);
        String outputFileName = OUT_FOLDER + fileName +  CSV_FILE;
        String[] headers = type != null && type.contains("line") ? HEADERS_LINES : HEADERS_POINTS;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setDelimiter(SEMICOLON_COLUMN_DELIMITER).setHeader(headers).build())) {

            if(type != null && type.contains("line")) {
                exportRouteConnectedByLines(csvPrinter, route);

            } else {
                exportRouteAsPoints(csvPrinter, route);
            }
            csvPrinter.flush();

        } catch (IOException e) {
            throw new RuntimeException("fail to write CSV file: " + e.getLocalizedMessage());
        }
    }

    private void exportRouteAsPoints(CSVPrinter csvPrinter, Route route) throws IOException {
        List<Position> positions = route.getPositions();
        for (Position position : positions) {
            csvPrinter.printRecord(
                    route.getCalculationMethod().name(),
                    route.getCalculationTriggerTime().toString(),
                    route.getRouteStart().toString(),
                    route.getRouteEnd().toString(),
                    route.getDevice().getFingerPrint(),
                    route.getDevice().getId(),
                    getGeopos(position),
                    getGeoposition(position),
                    position.getPositionTimestamp(),
                    position.getEstimatedFloor(),
                    position.getFloors(),
                    position.getSignal1().getId(),
                    position.getSignal1().getBeacon().getId(),
                    position.getSignal2().getId(),
                    position.getSignal2().getBeacon().getId(),
                    position.getSignal3().getId(),
                    position.getSignal3().getBeacon().getId()
            );
        }
    }

    private String getGeoposition(Position position){
        return position.getxCoordinate() +
                "," +
                position.getyCoordinate();
    }


    private String getGeopos(Position position){
        return "(" +
                position.getxCoordinate() +
                "|" +
                position.getyCoordinate() +
                "|" +
                position.getEstimatedFloor() +
                ")";
    }

    private void exportRouteConnectedByLines(CSVPrinter csvPrinter, Route route) throws IOException {
        // The Length of GeoShape Cell-value exceeds Java-String Maximum Length!! therefore a workaround is applied to print multiple line-records(but connected)
        // To Simplify we splitt each cell to contain max 100-GeoPoints instead of splitting by String Max Length
        List<List<Position>> partitions = createPartitions(route);
        for (List<Position> partition : partitions) {
            csvPrinter.printRecord(
                    route.getCalculationMethod().name(),
                    route.getCalculationTriggerTime().toString(),
                    route.getRouteStart().toString(),
                    route.getRouteEnd().toString(),
                    route.getDevice().getFingerPrint(),
                    route.getDevice().getId(),
                    getGeoShapeJson(partition),
                    partition.get(0).getPositionTimestamp(),
                    partition.get(0).getEstimatedFloor(),
                    partition.get(0).getFloors(),
                    partition.get(0).getSignal1().getId(),
                    partition.get(0).getSignal1().getBeacon().getId(),
                    partition.get(0).getSignal2().getId(),
                    partition.get(0).getSignal2().getBeacon().getId(),
                    partition.get(0).getSignal3().getId(),
                    partition.get(0).getSignal3().getBeacon().getId()
                    );
        }

    }

    private List<List<Position>> createPartitions(Route route) {
        List<Position> positions = route.getPositions();
        List<Position> exportPositions = new ArrayList<>();
        for(Position position : positions) {
            if(position.getxCoordinate().doubleValue() > 0.0 && position.getyCoordinate().doubleValue() > 0.0) {
                exportPositions.add(position);
            }
        }

        // ensure positions are sorted by PositionTimestamp
        Collections.sort(exportPositions);
        List<List<Position>> partitions = ListUtils.partition(exportPositions, 1);
        List<List<Position>> connectedPartitions = new ArrayList<>();
        for (List<Position> partition : partitions) {
            if (connectedPartitions.size() == 0) {
                connectedPartitions.add(partition);
            } else {
                List<Position> connectedPartition = new ArrayList<>();
                // Get Last Position from Previous Partition
                List<Position> previousPartition = connectedPartitions.get(connectedPartitions.size()-1);
                connectedPartition.add(previousPartition.get(previousPartition.size()-1));
                connectedPartition.addAll(partition);
                connectedPartitions.add(connectedPartition);
            }
        }
        connectedPartitions.remove(0);
        return connectedPartitions;
    }

    private String getGeoShapeJson(List<Position> positions) {
        // ensure positions are sorted by PositionTimestamp
        Collections.sort(positions);
        // compute GeoShapeJson Line containing all positions in correct order
        StringBuilder stringBuilder = new StringBuilder()
                .append("{\"coordinates\": [");
        for(Position position : positions) {
            stringBuilder.append("[")
                    .append(position.getyCoordinate())
                    .append(", ")
                    .append(position.getxCoordinate())
                    .append("]");
            if(!position.equals(positions.get(positions.size()-1))) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("], \"type\": \"LineString\"}");
        return stringBuilder.toString();
    }

    private String getOutFileName(Route route, String type) {
        String calculationMethod = route.getCalculationMethod().name();
        String deviceFingerprint = route.getDevice().getBrand();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_-_HH_mm_ss_SSS");
        String routeStartTime = sdf.format(route.getRouteStart());
        String routeEndTime = sdf.format(route.getRouteEnd());
        return type + "_" + calculationMethod + "_" + deviceFingerprint + "_" + routeStartTime + "-" + routeEndTime;
    }
}
