package org.jxls.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerFactory {
    private static Logger logger = LoggerFactory.getLogger(TransformerFactory.class);

    public static Transformer createTransformer(InputStream inputStream, OutputStream outputStream) {
        return PoiTransformer.createTransformer(inputStream, outputStream);
    }
}
