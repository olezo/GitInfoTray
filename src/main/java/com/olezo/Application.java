package com.olezo;

import com.olezo.dto.PullRequestDto;
import com.olezo.gui.Gui;
import com.olezo.mapper.GitHubRepositoryMapper;
import com.olezo.mapper.GutHubPullRequestMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@Slf4j
class Application {
    private static final String GITHUB_TOKEN_KEY = "GITHUB_TOKEN";
    private static final String TOKEN_MUST_BE_PROVIDED_MESSAGE = "Token must be provided";

    private final Gui gui = Gui.getInstance();
    private final Set<Long> pullRequestIds = new HashSet<>();
    private final GutHubPullRequestMapper gutHubPullRequestMapper = new GutHubPullRequestMapper();
    private final GitHubRepositoryMapper gitHubRepositoryMapper = new GitHubRepositoryMapper(gutHubPullRequestMapper);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @SneakyThrows
    void run() {
        gui.displayLoadingTrayMenu();

        String token = requiredNotBlank(System.getenv(GITHUB_TOKEN_KEY));
        var myAccount = new GitHubBuilder()
                .withAppInstallationToken(token)
                .build()
                .getMyself();

        log.debug("Scheduling tray menu update...");

        scheduler.scheduleAtFixedRate(() -> updateTrayMenuAndDisplayMessages(myAccount), 0, 5, TimeUnit.SECONDS);
    }

    @SneakyThrows
    private void updateTrayMenuAndDisplayMessages(GHMyself myAccount) {
        updateTrayMenu(myAccount);

        var repositories = myAccount.getAllRepositories()
                .values();
        var newPullRequests = getPullRequestDtos(repositories);

        displayNewPullRequestsMessages(newPullRequests);
    }

    @SneakyThrows
    private void updateTrayMenu(GHMyself myAccount) {
        var repositories = myAccount.getAllRepositories().values();
        var repositoryDescriptions = gitHubRepositoryMapper.mapToDtos(repositories);

        gui.updateTrayMenu(myAccount.getLogin(), repositoryDescriptions);
    }

    private List<PullRequestDto> getPullRequestDtos(Collection<GHRepository> repositories) {
        var pullRequests = getRepositoryPullRequests(repositories);
        var pullRequestDtos = gutHubPullRequestMapper.mapToPullRequestDtos(pullRequests);

        Set<Long> newPullRequestIds = newPullRequestIds(pullRequestDtos);
        pullRequestIds.addAll(newPullRequestIds);

        return getNewPullRequestDto(newPullRequestIds, pullRequestDtos);
    }

    private Set<Long> newPullRequestIds(List<PullRequestDto> pullRequests) {
        return pullRequests.stream()
                .map(PullRequestDto::getId)
                .filter(id -> !pullRequestIds.contains(id))
                .collect(Collectors.toSet());
    }

    private List<GHPullRequest> getRepositoryPullRequests(Collection<GHRepository> repositories) {
        return repositories.stream()
                .map(GHRepository::queryPullRequests)
                .map(GHPullRequestQueryBuilder::list)
                .flatMap(e -> StreamSupport.stream(e.spliterator(), true))
                .toList();
    }

    private List<PullRequestDto> getNewPullRequestDto(Set<Long> currentPullRequestIds, List<PullRequestDto> pullRequests) {
        return pullRequests.stream()
                .filter(pullRequest -> currentPullRequestIds.contains(pullRequest.getId()))
                .toList();
    }

    private void displayNewPullRequestsMessages(List<PullRequestDto> newPullRequests) {
        newPullRequests.forEach(pullRequest -> {
            var title = "New PR in " + pullRequest.getRepositoryName();

            gui.displayMessage(title, pullRequest.getTitle());
        });
    }

    private String requiredNotBlank(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(TOKEN_MUST_BE_PROVIDED_MESSAGE);
        }

        return value;
    }
}
