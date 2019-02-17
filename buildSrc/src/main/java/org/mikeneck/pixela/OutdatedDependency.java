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
package org.mikeneck.pixela;

import org.mikeneck.slack.Attachment;
import org.mikeneck.slack.Color;

public class OutdatedDependency {

  private String group;
  private String name;
  private String current;
  private String available;

  Attachment toAttachment(final Color color) {
    return new Attachment(color, this);
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(final String group) {
    this.group = group;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getCurrent() {
    return current;
  }

  public void setCurrent(final String current) {
    this.current = current;
  }

  public String getAvailable() {
    return available;
  }

  public void setAvailable(final String available) {
    this.available = available;
  }

  public OutdatedDependency() {}

  public OutdatedDependency(
      final String group, final String name, final String current, final String available) {
    this.group = group;
    this.name = name;
    this.current = current;
    this.available = available;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (!(object instanceof OutdatedDependency)) return false;

    final OutdatedDependency that = (OutdatedDependency) object;

    if (!group.equals(that.group)) return false;
    if (!name.equals(that.name)) return false;
    if (!current.equals(that.current)) return false;
    return available.equals(that.available);
  }

  @Override
  public int hashCode() {
    int result = group.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + current.hashCode();
    result = 31 * result + available.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return group + ":" + name + "[current:" + current + " -> update:" + available + "]";
  }

  public String mvnrepositoryUrl() {
    return "https://mvnrepository.com/artifact/" + group + "/" + name + "/" + available;
  }
}
