package org.jxls.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jxls.builder.JxlsStreaming;
import org.jxls.logging.JxlsLogger;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.transform.poi.PoiTransformerFactory;

public class TransformerFactory {
    static PoiTransformerFactory poiTransformerFactory = new PoiTransformerFactory();

    public static Transformer createTransformer(InputStream inputStream, OutputStream outputStream) {
        return poiTransformerFactory.create(inputStream, outputStream, JxlsStreaming.STREAMING_ON, new JxlsSlf4jLogger());
    }
}
