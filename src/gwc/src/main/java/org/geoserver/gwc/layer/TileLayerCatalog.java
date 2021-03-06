/* Copyright (c) 2012 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;

import java.util.Set;

public interface TileLayerCatalog {

    public Set<String> getLayerIds();

    public Set<String> getLayerNames();

    public String getLayerId(final String layerName);

    public String getLayerName(final String layerId);

    public GeoServerTileLayerInfo getLayerById(String id);

    public GeoServerTileLayerInfo getLayerByName(String layerName);

    public GeoServerTileLayerInfo delete(String tileLayerId);

    public GeoServerTileLayerInfo save(GeoServerTileLayerInfo newValue);

    public boolean exists(String layerId);

    public void initialize();
    
    public void reset();

}
