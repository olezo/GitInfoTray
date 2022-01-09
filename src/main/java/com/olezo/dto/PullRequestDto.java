package com.olezo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PullRequestDto {
    private long id;
    private String url;
    private String title;
    private String targetBranch;
    private String sourceBranch;
    private String repositoryName;
}
