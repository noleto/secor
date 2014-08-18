/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.secor.storage.seqfile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinterest.secor.common.LogFilePath;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.storage.StorageFactory;
import com.pinterest.secor.storage.Writer;
import com.pinterest.secor.util.ReflectionUtil;

/**
 * Storages messages to Hadoop Sequence File format.
 * 
 * @author Leonardo Noleto (noleto.leonardo@gmail.com)
 * 
 */
public class HadoopSequenceFileStorageFactory implements StorageFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(HadoopSequenceFileStorageFactory.class);

    private String mFileExtension;
    private CompressionCodec mCodec;

    public HadoopSequenceFileStorageFactory(SecorConfig config)
            throws Exception {
        if (config.getCompressionCodec() != null
                && !config.getCompressionCodec().isEmpty()) {
            mCodec = ((CompressionCodec) ReflectionUtil
                    .createCompressionCodec(config.getCompressionCodec()));
            mFileExtension = mCodec.getDefaultExtension();
        } else {
            mFileExtension = "";
        }
    }

    @Override
    public Writer createWriter(LogFilePath path) throws IOException {

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(config);

        Path fsPath = new Path(path.getLogFilePath());

        SequenceFile.Writer writer = null;
        if (mCodec != null) {
            writer = SequenceFile.createWriter(fs, config, fsPath,
                    LongWritable.class, BytesWritable.class,
                    SequenceFile.CompressionType.BLOCK, mCodec);
        } else {
            writer = SequenceFile.createWriter(fs, config, fsPath,
                    LongWritable.class, BytesWritable.class);
        }

        return new HadoopSequenceFileWriter(writer);
    }

    @Override
    public String getFileExtension() {
        return mFileExtension;
    }

    public CompressionCodec getCodec() {
        return mCodec;
    }
}
