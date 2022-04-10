package ch.zhaw.integration.beacons.rest.route.exporter;

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

    private static final String CALCULATION_METHOD = "calculation_method";
    private static final String CALC_TRIGGER_TIME = "calc_trigger_time";
    private static final String ROUTE_START_TIME = "route_start_time";
    private static final String ROUTE_END_TIME = "route_end_time";
    private static final String DEVICE_FINGERPRINT = "device_fingerprint";
    private static final String GEO_SHAPE = "Geo Shape";
    private static final String[] HEADERS_LINES = {
            CALCULATION_METHOD, CALC_TRIGGER_TIME, ROUTE_START_TIME, ROUTE_END_TIME, DEVICE_FINGERPRINT, GEO_SHAPE
    };
    private static final String GEOPOS = "geopos";
    private static final String GEOPOSITION = "Geoposition";
    private static final String[] HEADERS_POINTS = {
            CALCULATION_METHOD, CALC_TRIGGER_TIME, ROUTE_START_TIME, ROUTE_END_TIME, DEVICE_FINGERPRINT, GEOPOS, GEOPOSITION
    };

    public void createCsvPerRoute(List<Route> deviceRoutes, String type) {
        for(Route route : deviceRoutes) {
            exportRouteToCsv(route, type);
        }
    }

    private void exportRouteToCsv(Route route, String type) {
        String fileName = getOutFileName(route);
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
                    getGeopos(position),
                    getGeoposition(position)
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

    // TODO: check if its possible to connect route points with lines and refactor
    private void exportRouteConnectedByLines(CSVPrinter csvPrinter, Route route) throws IOException {
        // The Length of GeoShape Cell-value exceeds Java-String Maximum Length!! therefore a workaround is applied to print multiple line-records(but connected)
        // To Simplify we splitt each cell to contain max 100-GeoPoints instead of splitting by String Max Length
        List<List<Position>> partitions = createPartitions(route);
        for (List<Position> partition : partitions) {
            printRecord(csvPrinter, route, partition);
        }

    }

    private List<List<Position>> createPartitions(Route route) {
        List<Position> positions = route.getPositions();
        // ensure positions are sorted by PositionTimestamp
        Collections.sort(positions);
        List<List<Position>> partitions = ListUtils.partition(positions, 1);
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

    private void printRecord(CSVPrinter csvPrinter, Route route, List<Position> partition) throws IOException {
        String geoShape = getGeoShapeJson(partition);
        csvPrinter.printRecord(
                route.getCalculationMethod().name(),
                route.getCalculationTriggerTime().toString(),
                route.getRouteStart().toString(),
                route.getRouteEnd().toString(),
                route.getDevice().getFingerPrint(),
                geoShape
        );
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

    private String getOutFileName(Route route) {
        String calculationMethod = route.getCalculationMethod().name();
        String deviceFingerprint = route.getDevice().getBrand();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_-_HH_mm_ss_SSS");
        String routeStartTime = sdf.format(route.getRouteStart());
        String routeEndTime = sdf.format(route.getRouteEnd());
        return calculationMethod + "_" + deviceFingerprint + "_" + routeStartTime + "-" + routeEndTime;
    }
}
