package com.pinterest.secor.storage.plaintext;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinterest.secor.common.LogFilePath;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.storage.StorageFactory;
import com.pinterest.secor.storage.Writer;

public class PlainTextGzippedStorageFactory implements StorageFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(PlainTextGzippedStorageFactory.class);

    public PlainTextGzippedStorageFactory(SecorConfig config) {
        // nothing here
    }

    @Override
    public Writer createWriter(LogFilePath path) throws IOException {

        LOG.debug("Creating a Plain Text Gzipped writer for path '{}'.",
                path.getLogFilePath());

        return new PlainTextGzippedWriter(path.getLogFilePath());
    }

    @Override
    public String getFileExtension() {
        return ".gz";
    }
}
