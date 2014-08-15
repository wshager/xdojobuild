/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012-2013 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.dojobuild.xquery;

import java.util.List;
import java.util.Map;

import org.exist.dom.QName;
import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;
import org.exist.xquery.ErrorCodes.ErrorCode;

public class Module extends AbstractInternalModule {
	
	public final static String NAMESPACE_URI = "http://exist-db.org/dojobuild";
	public final static String PREFIX = "dojobuild";
	private final static String RELEASED_IN_VERSION = "eXist-2.0";
	private final static String DESCRIPTION = "Module for interacting with javascript.";
	
	public static final ErrorCode XDJB001 = new DebugErrorCode("XDJB001", "Not permitted. Only a DBA is allowed to execute this function.");
	public static final ErrorCode XDJB002 = new DebugErrorCode("XDJB002", "DOJO_HOME not set.");
	public static final ErrorCode XDJB003 = new DebugErrorCode("XDJB003", "IO error.");

    public static class DebugErrorCode extends ErrorCode {
        private DebugErrorCode(String code, String description) {
            super(new QName(code, NAMESPACE_URI, PREFIX), description);
        }
    }
    
	private final static FunctionDef[] functions = {
		//new FunctionDef(Run.signature, Run.class),
		new FunctionDef(Compile.signature, Compile.class)
	};

	public Module(Map<String, List<? extends Object>> parameters) {
		super(functions, parameters);
	}


	@Override
	public String getNamespaceURI() {
		return NAMESPACE_URI;
	}

	@Override
	public String getDefaultPrefix() {
		return PREFIX;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getReleaseVersion() {
		return RELEASED_IN_VERSION;
	}
}
