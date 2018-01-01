package io.gihub.mordijc.util;

import java.util.logging.Logger;

public final class Log {
    private static Logger logger = Logger.getLogger("ActParser");

    public static Logger getLogger() {
        return logger;
    }
}
