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

import bad.robot.http.configuration.Configurable;
import org.apache.http.params.HttpParams;

import java.net.URL;

import static bad.robot.http.apache.Coercions.asHttpHost;
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY;

public class ApacheHttpParameters {

    private final HttpParams parameters;

    public ApacheHttpParameters(HttpParams parameters) {
        this.parameters = parameters;
    }

    public Configurable configuration(final String parameter) {
        return new Configurable<Object>() {
            @Override
            public void setTo(Object value) {
                parameters.setParameter(parameter, value);
            }
        };
    }

    public Configurable<URL> defaultProxy() {
        return new Configurable<URL>() {
            @Override
            public void setTo(URL url) {
                parameters.setParameter(DEFAULT_PROXY, asHttpHost(url));
            }
        };
    }

}
