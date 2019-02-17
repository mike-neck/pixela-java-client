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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mikeneck.slack.Attachment;
import org.mikeneck.slack.Color;
import org.mikeneck.slack.SlackMessage;

public class OutdatedDependencies {

  private final Set<OutdatedDependency> dependencies;

  public OutdatedDependencies(final Set<OutdatedDependency> dependencies) {
    this.dependencies = dependencies;
  }

  public SlackMessage toSlackMessage() {
    final Iterator<Color> colorIterator = Color.infiniteIterator();
    final List<Attachment> attachments =
        dependencies
            .stream()
            .map(dep -> dep.toAttachment(colorIterator.next()))
            .collect(Collectors.toList());
    return new SlackMessage(attachments);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (!(object instanceof OutdatedDependencies)) return false;

    final OutdatedDependencies that = (OutdatedDependencies) object;

    return Objects.equals(dependencies, that.dependencies);
  }

  @Override
  public int hashCode() {
    return dependencies != null ? dependencies.hashCode() : 0;
  }

  public boolean upToDate() {
    return dependencies.isEmpty();
  }
}
