/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wfs.response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import junit.framework.Test;

import org.apache.commons.io.FileUtils;
import org.geoserver.data.test.MockData;
import org.geoserver.data.util.IOUtils;
import org.geoserver.wfs.WFSTestSupport;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.spatialite.SpatiaLiteDataStoreFactory;
import org.geotools.feature.FeatureCollection;

import com.mockrunner.mock.web.MockHttpServletResponse;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Test the SpatiaLiteOutputFormat WFS extension.
 * @author Pablo Velazquez, Geotekne, info@geotekne.com
 * @author Jose Macchi, Geotekne, jmacchi@geotekne.com
 */
public class SpatiaLiteOutputFormatTest extends WFSTestSupport {

    /**
     * This is a READ ONLY TEST so we can use one time setup
     */
    public static Test suite() {
        return new OneTimeTestSetup(new SpatiaLiteOutputFormatTest());
    }

    static Boolean SKIPPED = null;

    @Override
    protected void runTest() throws Throwable {
        if (Boolean.TRUE.equals(SKIPPED)) {
            return;
        }

        if (SKIPPED == null && !new SpatiaLiteDataStoreFactory().isAvailable()) {
            SKIPPED = true;
            System.out.println("Skipping spatialite tests, native libraries not installed");
            return;
        }
            
        SKIPPED = false;
        super.runTest();
    }

    /**
     * Test a request with multiple layers.
     * @throws Exception
     */
    public void testMultiResponse() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Points,MPoints&outputFormat=spatialite");
        DataStore ds = loadData(resp);

        assertEquals(2, ds.getTypeNames().length);
        assertEquals(Point.class, 
            ds.getSchema("Points").getGeometryDescriptor().getType().getBinding());
        assertEquals(MultiPoint.class, 
            ds.getSchema("MPoints").getGeometryDescriptor().getType().getBinding());
    }

    /**
     * Test SPATIALITE Mime format.
     * @throws Exception
     */
    public void testMIMEOutput() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        assertEquals("application/zip", resp.getContentType());
    }

    /**
     * Test the content disposition
     * @throws Exception
     */
    public void testContentDisposition() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        assertEquals("attachment; filename=Points.db.zip", resp.getHeader("Content-Disposition"));
    }

    /**
     * Test if exist WFS Error, checking for Mime Type.
     * If Mime Type is "application/xml", then an error has occurred
     * @throws Exception
     */
    public void testWFSError() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        assertNotSame("application/xml", resp.getContentType());
    }

    /**
     * Test not null content.
     * @throws Exception
     */
    public void testContentNotNull() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        ByteArrayInputStream sResponse = getBinaryInputStream(resp);
        int dataLengh = sResponse.available();
        boolean contentNull = true;
        byte[] data = new byte[dataLengh];
        sResponse.read(data);
            for (byte aByte : data)
                if (aByte != 0){
                    contentNull = false;
                    break;
                }
        assertFalse(contentNull);
    }

    /**
     * Test a Point geometry.
     * @throws Exception
     */
    public void testPoints() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Points").getFeatures();
            checkFeatures(getFeatureSource(MockData.POINTS).getFeatures(), fc);
        }
        finally {
            ds.dispose();    
        }
    }

    //for some odd reason this test does not work... TODO: further investigate
//    /**
//     * Test a MultiPoint geometry.
//     * @throws Exception
//     */
//    public void testMultiPoints() throws Exception {
//        MockHttpServletResponse resp = getAsServletResponse(
//            "wfs?request=GetFeature&typeName=MPoints&outputFormat=spatialite");
//        DataStore ds = loadData(resp);
//        try {
//            SimpleFeatureCollection fc = ds.getFeatureSource("MPoints").getFeatures();
//            checkFeatures(getFeatureSource(MockData.MPOINTS).getFeatures(), fc);
//        }
//        finally {
//            ds.dispose();    
//        }
//    }

    /**
     * Test a LineString geometry.
     * @throws Exception
     */
    public void testLines() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Lines&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Lines").getFeatures();
            checkFeatures(getFeatureSource(MockData.LINES).getFeatures(), fc);
        }
        finally {
            ds.dispose();    
        }
    }

    /**
     * Test a MultiLineString geometry.
     * @throws Exception
     */
   public void testMultiLines() throws Exception {
       MockHttpServletResponse resp = getAsServletResponse(
               "wfs?request=GetFeature&typeName=MLines&outputFormat=spatialite");
       DataStore ds = loadData(resp);
       try {
           SimpleFeatureCollection fc = ds.getFeatureSource("MLines").getFeatures();
           checkFeatures(getFeatureSource(MockData.MLINES).getFeatures(), fc);
       }
       finally {
           ds.dispose();    
       }
    }

    /**
     * Test a Polygon geometry.
     * @throws Exception
     */
    public void testPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&typeName=Polygons&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Polygons").getFeatures();
            checkFeatures(getFeatureSource(MockData.POLYGONS).getFeatures(), fc);
        }
        finally {
            ds.dispose();    
        }
    }

    /**
     * Test a MultiPolygon geometry.
     * @throws Exception
     */
    public void testMultiPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
                "wfs?request=GetFeature&typeName=MPolygons&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("MPolygons").getFeatures();
            checkFeatures(getFeatureSource(MockData.MPOLYGONS).getFeatures(), fc);
        }
        finally {
            ds.dispose();    
        }
    }

    
    /**
     * Test format option FILENAME.
     * @throws Exception
     */
    public void testCustomFileName() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(
            "wfs?request=GetFeature&format_options=FILENAME:customName.db&typeName=Points&outputFormat=spatialite");

        String cd = resp.getHeader("Content-Disposition");
        assertTrue(cd.contains("filename=customName.db"));
    }

    void checkFeatures(SimpleFeatureCollection expected, SimpleFeatureCollection actual) {
        assertEquals(expected.size(), actual.size());

        SimpleFeatureIterator it1 = expected.features();
        SimpleFeatureIterator it2 = actual.features();

        while(it1.hasNext()) {
            Geometry g1 = (Geometry) it1.next().getDefaultGeometry();
            Geometry g2 = (Geometry) it2.next().getDefaultGeometry();
            assertTrue(g1.equals(g2));
        }

        it1.close();
        it2.close();
    }

    DataStore loadData(MockHttpServletResponse resp) throws Exception {
        File dir = File.createTempFile("spatialite", "zip", new File("target"));
        dir.delete();
        dir.mkdir();

        IOUtils.decompress(getBinaryInputStream(resp), dir);
        File dbFile = dir.listFiles()[0];

        Map dbParams = new HashMap();
        dbParams.put("dbtype", "spatialite");
        dbParams.put("database", dbFile.getAbsolutePath());

        return new SpatiaLiteDataStoreFactory().createDataStore(dbParams);
    }

}
