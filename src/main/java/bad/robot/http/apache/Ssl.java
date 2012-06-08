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

import bad.robot.http.java.PlatformSslProtocolConfiguration;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;

public enum Ssl {
    enabled {
        @Override
        public SchemeSocketFactory getSocketFactory() {
            return SSLSocketFactory.getSocketFactory();
        }
    },
    naive {
        @Override
        public SchemeSocketFactory getSocketFactory() {
            setPlatformSslToAlwaysTrustCertificatesAndHosts();
            return SSLSocketFactory.getSocketFactory();
        }

        private void setPlatformSslToAlwaysTrustCertificatesAndHosts() {
            PlatformSslProtocolConfiguration configuration = new PlatformSslProtocolConfiguration();
            configuration.configurePlatformHostnameVerifier();
            configuration.configurePlatformTrustManager();
        }
    },
    disabled {
        @Override
        public SchemeSocketFactory getSocketFactory() {
            return PlainSocketFactory.getSocketFactory();
        }
    };

    public abstract SchemeSocketFactory getSocketFactory();
}
