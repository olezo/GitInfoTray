package com.olezo.mapper;

import com.olezo.dto.PullRequestDto;
import org.kohsuke.github.GHPullRequest;

import java.util.List;

public class GutHubPullRequestMapper {

    public List<PullRequestDto> mapToPullRequestDtos(List<GHPullRequest> pullRequests) {
        return pullRequests.stream()
                .map(this::mapToDto)
                .toList();
    }

    public PullRequestDto mapToDto(GHPullRequest pullRequest) {
        return PullRequestDto.builder()
                .id(pullRequest.getId())
                .title(pullRequest.getTitle())
                .sourceBranch(pullRequest.getHead().getRef())
                .targetBranch(pullRequest.getBase().getRef())
                .repositoryName(pullRequest.getRepository().getFullName())
                .build();
    }
}
