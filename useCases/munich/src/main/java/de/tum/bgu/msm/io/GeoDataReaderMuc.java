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
import org.geotools.data.DataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.shp.ShapefileReader;
//import org.matsim.core.utils.gis.ShapeFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.matsim.core.utils.gis.GeoFileReader;
import org.opengis.feature.simple.SimpleFeature;
import com.opencsv.CSVReader;
import org.geotools.data.DataStore;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GeoDataReaderMuc implements GeoDataReader {

    private static Logger logger = Logger.getLogger(GeoDataReaderMuc.class);

    private GeoData geoDataMuc;

    private final String SHAPE_IDENTIFIER = "CT_ID";
    private final String ZONE_ID_COLUMN = "CT_ID";

    public GeoDataReaderMuc(GeoData geoDataMuc) {
        this.geoDataMuc = geoDataMuc;
    }

    @Override
    public void readZoneCsv(String path) {
        logger.info("ZONESYSTEM.CSV");
//        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        try {
            FileReader filereader = new FileReader(path);
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
                logger.warn(lineNum);
                zoneIds[lineNum] = Integer.parseInt(next[0]);
                zoneAreas[lineNum] = Float.parseFloat(next[1]);
                ptDistances[lineNum] = Double.parseDouble(next[2]);
                areaTypes[lineNum] = Integer.parseInt(next[3]);
                regionColumn[lineNum] = Integer.parseInt(next[4]);
                lineNum++;
            }
            logger.info("lines read: " + lineNum);


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
        logger.warn("SALAAAM");
        if (path == null) {
            logger.error("No shape file found!");
            throw new RuntimeException("No shape file found!");
        }

        int counter = 0;

        try {
            // Create a file data store for the shapefile
            File shapeFile = new File(path);
            FileDataStore dataStore = FileDataStoreFinder.getDataStore(shapeFile);
            SimpleFeatureSource featureSource = dataStore.getFeatureSource();

            // Get feature collection and iterate over features
            SimpleFeatureCollection features = featureSource.getFeatures();
            SimpleFeatureIterator iterator = features.features();

            logger.warn("Reading features");

            try {
                while (iterator.hasNext()) {
//                    logger.info(counter + ": " + iterator.next().getAttributes().toString());
//                    break;
//                    logger.info();
                    SimpleFeature feature = iterator.next();
                    int zoneId = Integer.parseInt(feature.getAttribute(SHAPE_IDENTIFIER).toString());
                    Zone zone = geoDataMuc.getZones().get(zoneId);
                    if (zone != null) {
                        zone.setZoneFeature(feature);
                    } else {
                        counter++;
                    }
                }
            } finally {
                iterator.close(); // Important: always close the iterator
            }

            dataStore.dispose(); // Clean up resources

        } catch (Exception e) {
            logger.error("Error reading shapefile: " + e.getMessage(), e);
            throw new RuntimeException("Failed to read shapefile", e);
        }

        if(counter > 0) {
            logger.warn("There were " + counter + " shapes that do not exist in silo zone system");
        }
    }
}