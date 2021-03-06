package org.geoserver.web;

import java.util.logging.Level;

import org.apache.wicket.markup.html.basic.Label;
import org.geotools.factory.GeoTools;

/**
 * An about GeoServer page providing various bits of information.
 * 
 * @author Justin Deoliveira, OpenGeo
 *
 */
public class AboutGeoServerPage extends GeoServerBasePage {

    public AboutGeoServerPage() {
        add(new Label("geotoolsVersion", GeoTools.getVersion().toString()));
        add(new Label("geotoolsRevision", GeoTools.getBuildRevision().toString()));
        add(new Label("geowebcacheVersion", getGwcVersion()));
        add(new Label("geowebcacheRevision", getGwcRevision()));
    }

    public String getGwcVersion() {
        Package p = lookupGwcPackage();
        return p != null ? p.getSpecificationVersion() : null;
    }

    public String getGwcRevision() {
        Package p = lookupGwcPackage();
        return p != null ? p.getImplementationVersion() : null;
    }

    Package lookupGwcPackage() {
        try {
            return Package.getPackage("org.geowebcache");
        }
        catch(Exception e) {
            //be safe
            LOGGER.log(Level.FINE, "Error looking up org.geowebcache package", e);
        }
        return null;
    }
}
