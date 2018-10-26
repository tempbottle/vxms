/*
 * Copyright [2018] [Andy Moncsek]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jacpfx.vxms.rest;

import io.vertx.core.http.HttpMethod;
import org.jacpfx.vxms.rest.VxmsRESTRoutes.RestErrorConsumer;
import org.jacpfx.vxms.rest.VxmsRESTRoutes.RestHandlerConsumer;

public class MethodDescriptor {
  protected final HttpMethod httpMethod;
  protected final String path;
  protected final String[] consumes;
  protected final RestHandlerConsumer method;
  protected final RestErrorConsumer errorMethod;


  public MethodDescriptor(HttpMethod httpMethod, String path, String[] consumes,
      RestHandlerConsumer method,
      RestErrorConsumer errorMethod) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.consumes = consumes;
    this.method = method;
    this.errorMethod = errorMethod;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public String getPath() {
    return path;
  }

  public String[] getConsumes() {
    return consumes;
  }

  public RestHandlerConsumer getMethod() {
    return method;
  }

  public RestErrorConsumer getErrorMethod() {
    return errorMethod;
  }
}

