/* Copyright (c) 2001 - 2012 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.script.app;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.geoserver.script.ScriptHook;
import org.geoserver.script.ScriptPlugin;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Hook for "app" requests.
 * <p>
 * This class is responsible for adapting a raw http request into something that can be handled by
 * an app script. For example, a python extension could transform the request/response into a WSGI
 * request/response to be handled by the underlying app script.
 * </p>
 * <p>
 * Instances of this class must be thread safe.
 * </p>
 * @author Justin Deoliveira, OpenGeo
 *
 */
public class AppHook extends ScriptHook {

    public AppHook(ScriptPlugin plugin) {
        super(plugin);
    }

    /**
     * Handles a request.
     * @param request The http request.
     * @param response The http response.
     * @param engine The script engine of the appropriate type.
     * 
     */
    public void run(Request request, Response response, ScriptEngine engine) 
        throws ScriptException, IOException {
        if (engine instanceof Invocable) {
            try {
                ((Invocable)engine).invokeFunction("run", request, response);
            } catch (NoSuchMethodException e) {
                throw new ScriptException(e);
            }
        }
        else {
            throw new ScriptException("Engine does not implement Invocable, plugin must implement"
                + " custom app hook");
        }
    }

}
