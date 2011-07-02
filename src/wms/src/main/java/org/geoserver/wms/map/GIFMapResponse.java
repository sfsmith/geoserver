/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wms.map;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.geoserver.platform.ServiceException;
import org.geoserver.wms.MapProducerCapabilities;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSMapContent;
import org.geotools.image.ImageWorker;
import org.geotools.image.palette.InverseColorMapOp;

/**
 * Handles a GetMap request that spects a map in GIF format.
 * 
 * @author Didier Richard
 * @author Simone Giannecchini - GeoSolutions
 * @version $Id
 */
public final class GIFMapResponse extends RenderedImageMapResponse {

    /** the only MIME type this map producer supports */
    static final String MIME_TYPE = "image/gif";

    public GIFMapResponse(WMS wms) {
        super(MIME_TYPE, wms);
    }
    
    /** 
     * Default capabilities for GIF .
     * 
     * <p>
     * <ol>
     *         <li>tiled = supported</li>
     *         <li>multipleValues = unsupported</li>
     *         <li>paletteSupported = supported</li>
     *         <li>transparency = supported</li>
     * </ol>
     * 
     * <p>
     * We should soon support multipage tiff.
     */
    private static MapProducerCapabilities CAPABILITIES= new MapProducerCapabilities(true, false, true, true); 
    

    /**
     * Transforms the rendered image into the appropriate format, streaming to the output stream.
     * 
     * @param image
     *            The image to be formatted.
     * @param outStream
     *            The stream to write to.
     * 
     * @throws ServiceException
     *             not really.
     * @throws IOException
     *             if encoding to <code>outStream</code> fails.
     */
    public void formatImageOutputStream(RenderedImage originalImage, OutputStream outStream,
            WMSMapContent mapContent) throws ServiceException, IOException {
        // /////////////////////////////////////////////////////////////////
        //
        // Now the magic
        //
        // /////////////////////////////////////////////////////////////////
        InverseColorMapOp paletteInverter = mapContent.getPaletteInverter();
        RenderedImage renderedImage = super.forceIndexed8Bitmask(originalImage, paletteInverter);
        ImageWorker imageWorker = new ImageWorker(renderedImage);
        imageWorker.writeGIF(outStream, "LZW", 0.75f);
    }


    @Override
    public MapProducerCapabilities getCapabilities(String outputFormat) {
        return CAPABILITIES;
    }
}
