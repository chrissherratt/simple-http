/*
 * Copyright (c) 2011-2012, bad robot (london) ltd
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package bad.robot.http.apache;

import bad.robot.http.AutomaticRedirectHandling;
import org.apache.http.client.HttpClient;
import org.junit.Test;

import static bad.robot.http.apache.matchers.HttpParameterMatcher.parameter;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.minutes;
import static org.apache.http.client.params.ClientPNames.HANDLE_REDIRECTS;
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ApacheHttpClientBuilderTest {

    private final ApacheHttpClientBuilder builder = new ApacheHttpClientBuilder();

    @Test
    public void shouldDefaultConfigurationValues() {
        HttpClient client = builder.build();
        assertThat(client, parameter(HANDLE_REDIRECTS, is(true)));
        assertThat(client, parameter(CONNECTION_TIMEOUT, is(minutes(10).inMillis())));
        assertThat(client, parameter(SO_TIMEOUT, is(minutes(10).inMillis())));
    }

    @Test
    public void shouldConfigureAutomaticRedirectHandling() {
        assertThat(builder.with(AutomaticRedirectHandling.off()).build(), parameter(HANDLE_REDIRECTS, is(false)));
        assertThat(builder.with(AutomaticRedirectHandling.on()).build(), parameter(HANDLE_REDIRECTS, is(true)));
    }

    @Test
    public void shouldConfigureTimeouts() {
        HttpClient client = builder.with(millis(256)).build();
        assertThat(client, parameter(CONNECTION_TIMEOUT, is(256l)));
        assertThat(client, parameter(SO_TIMEOUT, is(256l)));
    }

}