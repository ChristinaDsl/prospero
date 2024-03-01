/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.prospero.cli;

import org.jboss.logging.Logger;
import org.wildfly.channel.Repository;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RepositoryDefinition {

    public static List<Repository> from(List<String> repos) throws ArgumentParsingException {
        ArrayList<Repository> repositories = new ArrayList<>(repos.size());
        for (int i = 0; i < repos.size(); i++) {
            final String text = repos.get(i);
            if(text.contains("::")) {
                final String[] parts = text.split("::");
                if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty() || !isValidUrlOrFilePath(parts[1])) {
                    throw CliMessages.MESSAGES.invalidRepositoryDefinition(text);
                }
                repositories.add(new Repository(parts[0], parts[1]));
            } else {
                if (!isValidUrlOrFilePath(text)) {
                    throw CliMessages.MESSAGES.invalidRepositoryDefinition(text);
                }
                repositories.add(new Repository("temp-repo-" + i, text));
            }
        }
        return repositories;
    }

    private static boolean isValidUrlOrFilePath(String text) {
        try {
            if (text.startsWith("file://..")) {
                Logger.getLogger("'file://../path/to/repo' is invalid as the format of file URL should be " +
                        "'file://<HOST>/<PATH>'");
                return false;
            } else if (text.startsWith("file") || text.startsWith("http") ){
                new URL(text);
                if (text.startsWith("http")){
                    return true;
                }
            }
            return filePathExists(text);
        } catch (URISyntaxException | InvalidPathException | MalformedURLException e) {
            return false;
        }
    }

    /* Pattern command, exists in order to recognize Windows file paths properly.
       For Windows systems, Path.of(Paths.get(url.toURI()).toString()) works correctly,
       whereas for Linux, Paths.get(url.getPath()).toAbsolutePath() is more suitable. */
    private static boolean filePathExists(String text) throws MalformedURLException, URISyntaxException {
        if (text.startsWith("file:")) {
            URL url = new URL(text);
            if (Pattern.compile("/[A-Z]:").matcher(text).find()) {
                return Files.exists(Path.of(Paths.get(url.toURI()).toString()));
            }
            return Files.exists(Paths.get(url.getPath()).toAbsolutePath());
        }
        return Files.exists(Paths.get(new URI(text).getPath()).toAbsolutePath());
    }
}
