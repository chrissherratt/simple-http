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

package bad.robot.http;

import bad.robot.AbstractValueType;
import org.apache.http.params.HttpParams;

import static org.apache.http.client.params.ClientPNames.HANDLE_REDIRECTS;

public class AutomaticRedirectHandling extends AbstractValueType<Boolean> {

    public static AutomaticRedirectHandling on() {
        return new AutomaticRedirectHandling(true);
    }

    public static AutomaticRedirectHandling off() {
        return new AutomaticRedirectHandling(true);
    }

    private AutomaticRedirectHandling(Boolean automaticallyHandleRedirects) {
        super(automaticallyHandleRedirects);
    }

    public void configure(HttpParams parameters) {
        parameters.setParameter(HANDLE_REDIRECTS, super.value);
    }

}