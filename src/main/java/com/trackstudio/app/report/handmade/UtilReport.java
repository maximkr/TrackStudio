package com.trackstudio.app.report.handmade;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class UtilReport {
    private static Log log = LogFactory.getLog(UtilReport.class);

    public static String formatXsl(String xml, String xsl) throws GranException{
        if (xsl == null || xsl.length() == 0) {
            return xml;
        }
        StringWriter buffer = new StringWriter();
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xsl.getBytes(Config.getEncoding()))));
            transformer.transform(new StreamSource(new ByteArrayInputStream(xml.getBytes(Config.getEncoding()))), new StreamResult(buffer));
        } catch (Exception e) {
            log.error("Transformer xml with xslt error: ",e);
            buffer.write("Transformer xml with xslt error: " + e.getMessage());
        }
        return buffer.toString();
    }
}
