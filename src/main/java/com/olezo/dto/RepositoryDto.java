package com.olezo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RepositoryDto {
    private long id;
    private String name;
    private String ownerLogin;
    private String repositoryUrl;
    private List<PullRequestDto> pullRequests;
    private Integer pullRequestCount;
}
