package com.olezo.client;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;

import java.util.*;
import java.util.stream.*;

public class GitHubClient {
    private static final String GITHUB_TOKEN_KEY = "GITHUB_TOKEN";
    private static final String TOKEN_MUST_BE_PROVIDED_MESSAGE = "Token must be provided";

    private final GHMyself myAccount;

    public GitHubClient() {
        myAccount = getMyAccount();
    }

    public String getLogin() {
        return myAccount.getLogin();
    }

    @SneakyThrows
    public Collection<GHRepository> getRepositories() {
        return myAccount.getAllRepositories().values();
    }

    public List<GHPullRequest> getPullRequests() {
        return getRepositories().stream()
                .map(GHRepository::queryPullRequests)
                .map(GHPullRequestQueryBuilder::list)
                .flatMap(e -> StreamSupport.stream(e.spliterator(), true))
                .toList();
    }

    @SneakyThrows
    private GHMyself getMyAccount() {
        String token = requiredNotBlank(System.getenv(GITHUB_TOKEN_KEY));
        return new GitHubBuilder()
                .withAppInstallationToken(token)
                .build()
                .getMyself();
    }

    private String requiredNotBlank(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(TOKEN_MUST_BE_PROVIDED_MESSAGE);
        }

        return value;
    }
}
