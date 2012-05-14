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

import bad.robot.http.Builder;
import com.google.code.tempusfugit.temporal.Duration;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.*;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import java.util.ArrayList;
import java.util.List;

import static com.google.code.tempusfugit.temporal.Duration.minutes;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static org.apache.http.client.params.ClientPNames.*;
import static org.apache.http.conn.routing.RouteInfo.LayerType.PLAIN;
import static org.apache.http.conn.routing.RouteInfo.TunnelType;
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;
import static org.apache.http.params.CoreProtocolPNames.USE_EXPECT_CONTINUE;

public class ApacheHttpClientBuilder implements Builder<org.apache.http.client.HttpClient> {

    private Duration timeout = minutes(10);
    private List<ApacheHttpAuthenticationCredentials> credentials = new ArrayList<ApacheHttpAuthenticationCredentials>();
    private HttpHost proxy;
    private Ssl ssl = Ssl.enabled;

    public static ApacheHttpClientBuilder anApacheClientWithShortTimeout() {
        return new ApacheHttpClientBuilder().with(seconds(5));
    }

    public ApacheHttpClientBuilder with(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public ApacheHttpClientBuilder withProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public ApacheHttpClientBuilder with(ApacheHttpAuthenticationCredentials login) {
        credentials.add(login);
        return this;
    }

    public ApacheHttpClientBuilder with(Ssl ssl) {
        this.ssl = ssl;
        return this;
    }
    
    public org.apache.http.client.HttpClient build() {
        HttpParams httpParameters = createAndConfigureHttpParameters();
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(httpParameters, createSchemeRegistry());
        DefaultHttpClient client = new ProxyTunnellingHttpClient(connectionManager, httpParameters, proxy);
        setupAuthorisation(client);
        return client;
    }

    private SchemeRegistry createSchemeRegistry() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, ssl.getSocketFactory()));
        return registry;
    }

    private HttpParams createAndConfigureHttpParameters() {
        HttpParams parameters = createHttpParametersViaNastyHackButBetterThanCopyAndPaste();
        parameters.setParameter(CONNECTION_TIMEOUT, (int) timeout.inMillis());
        parameters.setParameter(SO_TIMEOUT, (int) timeout.inMillis());
        parameters.setParameter(HANDLE_REDIRECTS, true);
        parameters.setParameter(ALLOW_CIRCULAR_REDIRECTS, true);
        parameters.setParameter(HANDLE_AUTHENTICATION, true);
        parameters.setParameter(USE_EXPECT_CONTINUE, true);
//        parameters.setParameter(DEFAULT_PROXY, proxy);
        HttpClientParams.setRedirecting(parameters, true);
        return parameters;
    }

    protected HttpParams createHttpParametersViaNastyHackButBetterThanCopyAndPaste() {
        return new DefaultHttpClient() {
            @Override
            protected HttpParams createHttpParams() {
                return super.createHttpParams();
            }
        }.createHttpParams();
    }

    private void setupAuthorisation(DefaultHttpClient client) {
        for (ApacheHttpAuthenticationCredentials credentials : this.credentials)
            client.getCredentialsProvider().setCredentials(credentials.getScope(), credentials.getUser());
    }

    private static class ProxyTunnellingHttpClient extends DefaultHttpClient {
        public ProxyTunnellingHttpClient(ThreadSafeClientConnManager connectionManager, HttpParams httpParameters, final HttpHost proxy) {
            super(connectionManager, httpParameters);
            setRoutePlanner(new HttpRoutePlanner() {
                @Override
                public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
                    HttpHost[] proxies = {new HttpHost("localhost", 8081), proxy};
                    return new HttpRoute(target, ConnRouteParams.getLocalAddress(request.getParams()), proxies, "https".equalsIgnoreCase(target.getSchemeName()), TunnelType.TUNNELLED, PLAIN);
                }
            });
        }

        @Override
        protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler stateHandler, HttpParams params) {
            return new ProxyTunnellingRequestDirector(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, targetAuthHandler, proxyAuthHandler, stateHandler, params);
        }
    }
}
