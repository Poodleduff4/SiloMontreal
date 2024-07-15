package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;
import com.opencsv.CSVReader;

import java.io.FileReader;

public class GeoDataReaderMuc implements GeoDataReader {

    private static Logger logger = Logger.getLogger(GeoDataReaderMuc.class);

    private GeoData geoDataMuc;

    private final String SHAPE_IDENTIFIER = "DAUID";
    private final String ZONE_ID_COLUMN = "Zone";

    public GeoDataReaderMuc(GeoData geoDataMuc) {
        this.geoDataMuc = geoDataMuc;
    }

    @Override
    public void readZoneCsv(String path) {
//        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        try {
            FileReader filereader = new FileReader("/Volumes/SD/silo2/useCases/munich/test/gma/input/zoneSystem.csv");
            CSVReader csvReader = new CSVReader(filereader);
            int numZones = 5540;
            int[] zoneIds = new int[numZones]; // = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
            float[] zoneAreas = new float[numZones]; // = zonalData.getColumnAsFloat("Area");

            double[] ptDistances = new double[numZones]; // = zonalData.getColumnAsDouble("distanceToTransit");

            int[] areaTypes = new int[numZones]; // = zonalData.getColumnAsInt("BBSR_Type");

            int[] regionColumn = new int[numZones]; // = zonalData.getColumnAsInt("Region");
            String[] next;
            int lineNum = 0;
            csvReader.readNext();
            while ((next = csvReader.readNext()) != null) {
//                if (lineNum == 0)
//                    continue;
                System.out.println(lineNum);
                zoneIds[lineNum] = Integer.parseInt(next[0]);
                zoneAreas[lineNum] = Float.parseFloat(next[1]);
                ptDistances[lineNum] = Double.parseDouble(next[2]);
                areaTypes[lineNum] = Integer.parseInt(next[3]);
                regionColumn[lineNum] = Integer.parseInt(next[4]);
                lineNum++;
            }
            System.out.println("lines read: " + lineNum);


            for (int i = 0; i < zoneIds.length; i++) {
                AreaTypes.SGType type = AreaTypes.SGType.valueOf(areaTypes[i]);
                Region region;
                int regionId = regionColumn[i];
                if (geoDataMuc.getRegions().containsKey(regionId)) {
                    region = geoDataMuc.getRegions().get(regionId);
                } else {
                    region = new RegionImpl(regionId);
                    geoDataMuc.addRegion(region);
                }
                ZoneMuc zone = new ZoneMuc(zoneIds[i], zoneAreas[i], type, ptDistances[i], region);
                region.addZone(zone);
                geoDataMuc.addZone(zone);
            }
        }catch(Exception e){
            System.out.println(e);}
    }

    @Override
    public void readZoneShapefile(String path) {
        if (path == null) {
            logger.error("No shape file found!");
            throw new RuntimeException("No shape file found!");
        }
        int counter = 0;
        for (SimpleFeature feature : ShapeFileReader.getAllFeatures(path)) {
            int zoneId = Integer.parseInt(feature.getAttribute(SHAPE_IDENTIFIER).toString());
            Zone zone = geoDataMuc.getZones().get(zoneId);
            if (zone != null) {
                zone.setZoneFeature(feature);
            } else {
                counter++;
            }
        }
        if(counter > 0) {
            logger.warn("There were " + counter + " shapes that do not exist in silo zone system");
        }
    }
}