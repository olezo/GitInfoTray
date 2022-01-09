package com.olezo.service;

import com.olezo.client.GitHubClient;
import com.olezo.dto.PullRequestDto;
import com.olezo.dto.RepositoryDto;
import com.olezo.mapper.GitHubRepositoryMapper;
import com.olezo.mapper.GutHubPullRequestMapper;
import com.olezo.repository.PullRequestRepository;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.*;

public class GitHubService {
    private final GutHubPullRequestMapper gutHubPullRequestMapper = new GutHubPullRequestMapper();
    private final GitHubRepositoryMapper gitHubRepositoryMapper = new GitHubRepositoryMapper(gutHubPullRequestMapper);

    private final GitHubClient gitHubClient = new GitHubClient();
    private final PullRequestRepository repository = new PullRequestRepository();

    public String getLogin() {
        return gitHubClient.getLogin();
    }

    @SneakyThrows
    public List<RepositoryDto> getRepositories() {
        var repositories = gitHubClient.getRepositories();

        return gitHubRepositoryMapper.mapToDtos(repositories);
    }

    public List<PullRequestDto> getPullRequests() {
        var pullRequests = gitHubClient.getPullRequests();

        return gutHubPullRequestMapper.mapToPullRequestDtos(pullRequests);
    }

    public List<PullRequestDto> getNewPullRequests() {
        var pullRequests = getPullRequests();

        List<PullRequestDto> newPullRequests = getNewPullRequests(pullRequests);

        var newPullRequestIds = newPullRequests.stream()
                .map(PullRequestDto::getId)
                .collect(Collectors.toSet());

        repository.addPullRequestsIds(newPullRequestIds);

        return newPullRequests;
    }

    private List<PullRequestDto> getNewPullRequests(List<PullRequestDto> pullRequests) {
        var storedPullRequestIds = repository.getStoredPullRequestIds();

        return pullRequests.stream()
                .filter(pullRequest -> !storedPullRequestIds.contains(pullRequest.getId()))
                .collect(Collectors.toList());
    }
}
