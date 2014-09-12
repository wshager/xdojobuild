package org.exist.dojobuild.xquery;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.OutputStream;

import org.exist.http.servlets.ResponseWrapper;
import org.exist.xquery.functions.response.ResponseModule;
import org.exist.dom.QName;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

public class Compile extends BasicFunction {
	private static String OS = System.getProperty("os.name").toLowerCase();
	public final static FunctionSignature signature =
	new FunctionSignature(
		new QName("compile", Module.NAMESPACE_URI, Module.PREFIX),
		"Compile dojo application from xquery.",
		new SequenceType[] {
			new FunctionParameterSequenceType("text", Type.STRING, Cardinality.ZERO_OR_MORE, "The profile to compile")
		},
		new SequenceType(Type.ITEM, Cardinality.EMPTY),
		true);
	public Compile(XQueryContext context) {
		super(context, signature);
	}
	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence)
		throws XPathException {
		if(!context.getSubject().hasDbaRole()) throw new XPathException(this,Module.XDJB001,"Only a DBA is allowed to execute this function");
		if(args[0].isEmpty()) {
			return Sequence.EMPTY_SEQUENCE;
		}
		ArrayList<String> strlist = new ArrayList<String>();
		for (SequenceIterator i = args[0].iterate(); i.hasNext();) {
			strlist.add(i.nextItem().getStringValue());
		}
		// about the execution environment of a script.
		String dojohome = System.getenv("DOJO_HOME");
		if(dojohome==null) throw new XPathException(this,Module.XDJB002,"DOJO_HOME not set");
		final ResponseModule myModule = (ResponseModule) context.getModule(ResponseModule.NAMESPACE_URI);
		// response object is read from global variable $response
		final Variable respVar = myModule.resolveVariable(ResponseModule.RESPONSE_VAR);
		if((respVar == null) || (respVar.getValue() == null)) {
		throw (new XPathException(this, "No response object found in the current XQuery context."));
		}
		if(respVar.getValue().getItemType() != Type.JAVA_OBJECT) {
		throw (new XPathException(this, "Variable $response is not bound to an Java object."));
		}
		final JavaObjectValue respValue = (JavaObjectValue) respVar.getValue().itemAt(0);
		if(!"org.exist.http.servlets.HttpResponseWrapper".equals(respValue.getObject().getClass().getName())) {
		throw (new XPathException(this, signature.toString() + " can only be used within the EXistServlet or XQueryServlet"));
		}
		final ResponseWrapper response = (ResponseWrapper) respValue.getObject();
		response.setHeader("Content-Type", "text/plain");
		String ext = OS.indexOf("win") >= 0 ? "bat" : "sh";
		File baseUrl = new File(dojohome + "/../util/buildscripts/build." + ext);
		String profile = strlist.get(0);
		try {
			final ProcessBuilder pb = new ProcessBuilder(baseUrl.toString(),"-p",profile);
			pb.directory(new File(dojohome));
			pb.redirectErrorStream(true);
			final Process process = pb.start();
			InputStream is = process.getInputStream();
			BinaryValue data = BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(),is);
			final OutputStream os = response.getOutputStream();
			data.streamBinaryTo(response.getOutputStream());
			os.close();
			//commit the response
			response.flushBuffer();
		} catch(Exception e) {
			throw new XPathException(this,Module.XDJB003,"IO exception while streaming data: "+e.toString());
		}
		return Sequence.EMPTY_SEQUENCE;
	}
}
