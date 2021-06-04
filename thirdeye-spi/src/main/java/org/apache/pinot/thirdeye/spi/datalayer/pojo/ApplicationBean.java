/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.pinot.thirdeye.spi.datalayer.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.pinot.thirdeye.spi.datalayer.dto.AbstractDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationBean extends AbstractDTO {

  String application;
  String recipients;

  public String getApplication() {
    return application;
  }

  public ApplicationBean setApplication(final String application) {
    this.application = application;
    return this;
  }

  public String getRecipients() {
    return recipients;
  }

  public ApplicationBean setRecipients(final String recipients) {
    this.recipients = recipients;
    return this;
  }
}