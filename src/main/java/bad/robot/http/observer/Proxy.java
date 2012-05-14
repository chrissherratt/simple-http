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

package bad.robot.http.observer;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.*;

import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.contains;

public class Proxy {

    public static final String RedirectPortHeader = "bad.robot.http.original.port";

    private final DefaultHttpProxyServer proxy;

    public Proxy() {
        proxy = new DefaultHttpProxyServer(8081, new HttpRequestFilter() {
            @Override
            public void filter(HttpRequest request) {
                String pragma = request.getHeader("Pragma");
                if (contains(pragma, RedirectPortHeader))
                    request.setUri(request.getUri());
                System.out.println(request);
            }
        }, new HashMap<String, HttpFilter>() {
            @Override
            public HttpFilter get(Object key) {
                return new DefaultHttpFilter(new HttpResponseFilter() {
                    @Override
                    public HttpResponse filterResponse(HttpResponse response) {
                        System.out.println(response);
                        return response;
                    }
                });
            }
        }
        );
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                proxy.start();
            }
        }.start();
    }

    public void stop() {
        proxy.stop();
    }

    public static void main(String[] args) {
        new Proxy().start();
    }
}
