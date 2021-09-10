package org.jxls.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.expression.ExpressionEvaluator;
import org.jxls.expression.ExpressionEvaluatorFactory;
import org.jxls.expression.ExpressionEvaluatorFactoryJexlImpl;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OsgiJxlsHelper extends JxlsHelper {

    public static JxlsHelper getInstance() {
        return new OsgiJxlsHelper();
    }

    private ExpressionEvaluatorFactory expressionEvaluatorFactory = new ExpressionEvaluatorFactoryJexlImpl();
    private ExpressionEvaluatorFactoryJexlImpl expressionEvaluatorFactoryJexl = new ExpressionEvaluatorFactoryJexlImpl();

    @Override
    public Transformer createTransformer(InputStream templateStream, OutputStream targetStream) {
        return PoiTransformer.createTransformer(templateStream, targetStream);
    }

    public static String getProperty(final String key, final String defaultValue) {
        return null;
    }

    /**
     * @return current {@link ExpressionEvaluatorFactory} implementation
     */
    @Override
    public ExpressionEvaluatorFactory getExpressionEvaluatorFactory() {
        return expressionEvaluatorFactory;
    }

    /**
     * Creates {@link ExpressionEvaluator} instance for evaluation of the given expression
     *
     * @param expression expression to evaluate
     * @return {@link ExpressionEvaluator} instance for evaluation the passed expression
     */
    @Override
    public ExpressionEvaluator createExpressionEvaluator(final String expression) {
        return getExpressionEvaluatorFactory().createExpressionEvaluator(expression);
    }


}
