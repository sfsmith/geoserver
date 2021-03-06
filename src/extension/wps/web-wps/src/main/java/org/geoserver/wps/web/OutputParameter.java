/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.geoserver.wps.ppio.ComplexPPIO;
import org.geoserver.wps.ppio.ProcessParameterIO;
import org.geoserver.wps.process.GeoServerProcessors;
import org.geotools.data.Parameter;
import org.geotools.process.ProcessFactory;
import org.opengis.feature.type.Name;

/**
 * A single output parameter, along with the chosen output mime type and the output inclusion flag
 * 
 * @author Andrea Aime - OpenGeo
 */
@SuppressWarnings("serial")
class OutputParameter implements Serializable {
    Name processName;

    String paramName;

    String mimeType;

    boolean include = true;

    public OutputParameter(Name processName, String paramName) {
        this.processName = processName;
        this.paramName = paramName;
        Parameter<?> p = getParameter();
        this.mimeType = getDefaultMime();
    }

    String getDefaultMime() {
        if (!isComplex()) {
            return null;
        } else {
            return ((ComplexPPIO) getProcessParameterIO().get(0)).getMimeType();
        }
    }

    public List<String> getSupportedMime() {
        List<String> results = new ArrayList<String>();
        for (ProcessParameterIO ppio : getProcessParameterIO()) {
            ComplexPPIO cp = (ComplexPPIO) ppio;
            results.add(cp.getMimeType());
        }
        return results;
    }

    public boolean isComplex() {
        List<ProcessParameterIO> ppios = getProcessParameterIO();
        return ppios.size() > 0 && ppios.get(0) instanceof ComplexPPIO;
    }

    List<ProcessParameterIO> getProcessParameterIO() {
        return ProcessParameterIO.findAll(getParameter(), null);
    }

    ProcessFactory getProcessFactory() {
        return GeoServerProcessors.createProcessFactory(processName);
    }

    Parameter<?> getParameter() {
        return getProcessFactory().getResultInfo(processName, null).get(paramName);
    }

}