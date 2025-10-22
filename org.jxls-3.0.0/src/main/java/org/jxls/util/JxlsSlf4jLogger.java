package org.jxls.util;

import org.jxls.common.Context;
import org.jxls.common.JxlsException;
import org.jxls.logging.JxlsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JxlsSlf4jLogger implements JxlsLogger {
    static Logger log = LoggerFactory.getLogger(JxlsSlf4jLogger.class);
    public void handleCellException(Exception e, String cell, Context context) {
        throw new JxlsException("Failed to write a cell with " + cell, e);
    }

    public void handleFormulaException(Exception e, String cell, String formula) {
        throw new JxlsException("Failed to set formula \"" + formula + "\" into cell " + cell, e);
    }

    public void handleTransformException(Exception e, String sourceCell, String targetCell) {
        throw new JxlsException("Failed to transform " + sourceCell + " into " + targetCell + "\n" + e.getMessage(), e);
    }

    public void handleUpdateRowHeightsException(Exception e, int sourceRow, int targetRow) {
        throw new JxlsException("Failed to update row height for source row " + sourceRow + " and target row " + targetRow, e);
    }

    public void handleEvaluationException(Exception e, String cell, String expression) {
        throw new JxlsException("Failed to evaluate collection expression \"" + expression + "\" in each command at " + cell, e);
    }

    public void handleGetObjectPropertyException(Exception e, Object obj, String propertyName) {
        throw new JxlsException("Failed to get property '" + propertyName + "' of object " + obj, e);
    }

    public void handleSetObjectPropertyException(Exception e, Object obj, String propertyName, String propertyValue) {
        throw new JxlsException("Failed to set property '" + propertyName + "' to value '" + propertyValue + "' for object " + obj, e);
    }

    public void handleSheetNameChange(String invalidSheetName, String newSheetName) {
        this.info("Change invalid sheet name " + invalidSheetName + " to " + newSheetName);
    }

    public void debug(String msg) {
    }

    public void info(String msg) {
        log.info(msg);
    }

    public void warn(String msg) {
        this.write("WARN", msg, (Throwable) null);
    }

    public void warn(Throwable e, String msg) {
        this.write("WARN", msg, e);
    }

    public void error(String msg) {
        this.write("ERROR", msg, null);
        throw new JxlsException(msg);
    }

    public void error(Throwable e, String msg) {
        this.write("ERROR", msg, e);
        throw new JxlsException(msg, e);
    }

    protected void write(String level, String msg, Throwable e) {
        if (level.endsWith("WARN")) {
            log.warn(msg, e);
        } else if (level.endsWith("ERROR")) {
            log.error(msg, e);
        }
    }
}