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
package org.mikeneck;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.mikeneck.pixela.OutdatedDependencies;
import org.mikeneck.pixela.OutdatedDependency;
import org.mikeneck.slack.SlackMessage;

public class DependencyUpdatesSlackNotificationTask extends DefaultTask {

  private final Codec codec = new Codec();

  @InputFiles private List<File> dependencyUpdatesFiles;

  public void setDependencyUpdatesFiles(final List<File> dependencyUpdatesFiles) {
    this.dependencyUpdatesFiles = dependencyUpdatesFiles;
  }

  private String slackUrl;

  public void setSlackUrl(final String slackUrl) {
    this.slackUrl = slackUrl;
  }

  @TaskAction
  public void sendMessage() {
    if (dependencyUpdatesFiles.isEmpty()) {
      getLogger().info("Finish without send notification. Because files aren't registered.");
      return;
    }
    final OutdatedDependencies outdatedDependencies = new OutdatedDependencies(loadFiles());
    if (outdatedDependencies.upToDate()) {
      getLogger().info("Finish without sending notification. Because updates were not found.");
      return;
    }
    final SlackMessage slackMessage = outdatedDependencies.toSlackMessage();
    if (slackUrl == null || slackUrl.isEmpty() || slackUrl.isBlank()) {
      getLogger()
          .info("Finish without sending notification. Because slack incoming webhooks is not set.");
      return;
    }
    final Optional<SlackException> throwable =
        new MessageSender(codec, URI.create(slackUrl), slackMessage).sendMessage();
    throwable.ifPresent(e -> getLogger().error("Failed to send notification.", e));
    throwable.ifPresent(
        e -> {
          throw e;
        });
  }

  private Set<OutdatedDependency> loadFiles() {
    if (dependencyUpdatesFiles.isEmpty()) {
      return Set.of();
    }
    final Set<OutdatedDependency> dependencies = new HashSet<>();
    for (final File dependencyUpdatesFile : dependencyUpdatesFiles) {
      if (!dependencyUpdatesFile.exists()) {
        continue;
      }
      final List<OutdatedDependency> outdatedDependencies =
          codec.loadFileAsList(dependencyUpdatesFile, OutdatedDependency.class);
      dependencies.addAll(outdatedDependencies);
    }
    return dependencies;
  }
}
