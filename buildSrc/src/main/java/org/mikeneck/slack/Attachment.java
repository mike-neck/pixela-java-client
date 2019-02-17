/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.slack;

import org.mikeneck.pixela.OutdatedDependency;

public class Attachment {
  private final Color color;
  private final OutdatedDependency outdatedDependency;

  public Attachment(final Color color, final OutdatedDependency outdatedDependency) {
    this.color = color;
    this.outdatedDependency = outdatedDependency;
  }

  public String getColor() {
    return color.rgb;
  }

  public String getTitle() {
    return outdatedDependency.getName();
  }

  public String getTitleLink() {
    return outdatedDependency.mvnrepositoryUrl();
  }

  public String getText() {
    return "current: "
        + outdatedDependency.getCurrent()
        + ", update: "
        + outdatedDependency.getAvailable();
  }
}
