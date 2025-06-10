package ch.totifle.uav;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

public class Coordinate {

    public double x, y, z;
    
    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static final CoordinateReferenceSystem WGS84 = crsFactory.createFromName("epsg:4326");
    private static final CoordinateReferenceSystem LV95 = crsFactory.createFromName("epsg:2056");

    private static final CoordinateTransform wgsToLV = ctFactory.createTransform(WGS84, LV95);
    //private static final CoordinateTransform lvToWGS = ctFactory.createTransform(LV95, WGS84);

    public Coordinate(double e, double n, double z){
        x = e;
        y = n;
        this.z = z;
    }

    public Coordinate(ProjCoordinate wgs40Coordinate, double z){
        ProjCoordinate result = new ProjCoordinate();
        wgsToLV.transform(wgs40Coordinate, result);

        this.x = result.x;
        this.y = result.y;
        this.z = z;
    }

    public void setFromWSG40(ProjCoordinate wgs40Coordinate){
    
        ProjCoordinate result = new ProjCoordinate();
        wgsToLV.transform(wgs40Coordinate, result);

        this.x = result.x;
        this.y = result.y;

    }
}
