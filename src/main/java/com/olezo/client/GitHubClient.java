package com.olezo.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;

import java.util.*;
import java.util.stream.*;

@Slf4j
public class GitHubClient {
    private static final String GITHUB_TOKEN_KEY = "GITHUB_TOKEN";
    private static final String TOKEN_MUST_BE_PROVIDED_MESSAGE = "Token must be provided";

    private final GHMyself myAccount;

    // TODO: Temporary cache
    private final Collection<GHRepository> repositoriesCache = new ArrayList<>();

    public GitHubClient() {
        log.info("Connecting to GitHub...");

        myAccount = getMyAccount();

        log.info("Connected");
    }

    public String getLogin() {
        return myAccount.getLogin();
    }

    @SneakyThrows
    public Collection<GHRepository> getRepositories() {
        Collection<GHRepository> repositories = myAccount.getAllRepositories().values();

        updateCache(repositories);

        return repositories;
    }

    public List<GHPullRequest> getPullRequests() {
        return repositoriesCache.stream()
                .map(GHRepository::queryPullRequests)
                .map(GHPullRequestQueryBuilder::list)
                .flatMap(e -> StreamSupport.stream(e.spliterator(), true))
                .toList();
    }

    @SneakyThrows
    private GHMyself getMyAccount() {
        String token = requiredNotBlank(getEnvironmentVariableOrSystemProperty(GITHUB_TOKEN_KEY));
        return new GitHubBuilder()
                .withAppInstallationToken(token)
                .build()
                .getMyself();
    }

    private String getEnvironmentVariableOrSystemProperty(String key) {
        log.debug("Environment Variable {} is present: {}", key, StringUtils.isNoneBlank(System.getenv(key)));
        log.debug("System Property {} is present: {}", key, StringUtils.isNoneBlank(System.getProperty(key)));

        return Optional.ofNullable(System.getenv(key))
                .orElse(System.getProperty(key));
    }

    private String requiredNotBlank(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(TOKEN_MUST_BE_PROVIDED_MESSAGE);
        }

        return value;
    }

    private void updateCache(Collection<GHRepository> repositories) {
        repositoriesCache.clear();
        repositoriesCache.addAll(repositories);
    }
}
