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
package com.pinterest.secor.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinterest.secor.tools.TestLogMessageProducer;

/**
 * Test log message producer main.
 * 
 * Run: $ cd optimus/secor $ mvn package $ cd target $ java -ea
 * -Dlog4j.configuration=log4j.dev.properties
 * -Dconfig=secor.dev.backup.properties \ -cp "secor-0.1-SNAPSHOT.jar:lib/*"
 * com.pinterest.secor.main.TestLogMessageProducerMain \ -t topic -m
 * num_messages -p num_producer_threads
 * 
 * @author Pawel Garbacki (pawel@pinterest.com)
 */
public class TestLogMessageProducerMain {
    private static final Logger LOG = LoggerFactory
            .getLogger(TestLogMessageProducerMain.class);

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("topic")
                .withDescription("topic to post to").hasArg()
                .withArgName("<topic>").withType(String.class).create("t"));
        options.addOption(OptionBuilder.withLongOpt("messages")
                .withDescription("number of messages per producer to post")
                .hasArg().withArgName("<num_messages>").withType(Number.class)
                .create("m"));
        options.addOption(OptionBuilder.withLongOpt("producers")
                .withDescription("number of producer threads").hasArg()
                .withArgName("<num_producer_threads>").withType(Number.class)
                .create("p"));

        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    public static void main(String[] args) {
        try {
            CommandLine commandLine = parseArgs(args);
            String topic = commandLine.getOptionValue("topic");
            int messages = ((Number) commandLine
                    .getParsedOptionValue("messages")).intValue();
            int producers = ((Number) commandLine
                    .getParsedOptionValue("producers")).intValue();
            for (int i = 0; i < producers; ++i) {
                TestLogMessageProducer producer = new TestLogMessageProducer(
                        topic, messages);
                producer.start();
            }
        } catch (Throwable t) {
            LOG.error("Log message producer failed", t);
            System.exit(1);
        }
    }
}
