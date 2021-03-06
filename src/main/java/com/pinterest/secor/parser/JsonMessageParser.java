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
package com.pinterest.secor.parser;

import java.io.UnsupportedEncodingException;

import com.jayway.jsonpath.JsonPath;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;

/**
 * JsonMessageParser extracts timestamp field (specified by
 * 'message.timestamp.name') from JSON data and partitions data by date.
 */
public class JsonMessageParser extends TimestampedMessageParser {
    public JsonMessageParser(SecorConfig config) {
        super(config);
    }

    @Override
    public long extractTimestampMillis(final Message message) throws Exception,
            UnsupportedEncodingException {
        byte[] json = message.getPayload();

        if (json != null && json.length > 0) {
            String timestampFieldPath = mConfig.getMessageTimestampName();
            Object fieldValue = JsonPath.read(new String(json, "UTF-8"), "$."
                    + timestampFieldPath);
            if (fieldValue != null) {
                long timestamp = 0;
                if (fieldValue instanceof Number) {
                    timestamp = ((Number) fieldValue).longValue();
                } else {
                    // Sadly, I don't know of a better way to support all
                    // numeric types in Java
                    try {
                        timestamp = Long.valueOf(fieldValue.toString());
                    } catch (NumberFormatException e) {
                        timestamp = Double.valueOf(fieldValue.toString())
                                .longValue();
                    }
                }
                return toMillis(timestamp);
            }
        } else {
            throw new Exception(
                    "Unable to extract timestamp field from an empty message!");
        }
        return 0;
    }

}
