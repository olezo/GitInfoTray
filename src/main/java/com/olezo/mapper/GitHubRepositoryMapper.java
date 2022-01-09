package com.olezo.mapper;

import com.olezo.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.kohsuke.github.GHRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class GitHubRepositoryMapper {
    private final GutHubPullRequestMapper pullRequestMapper;

    public List<RepositoryDto> mapToDtos(Collection<GHRepository> repositories) {
        return repositories
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private RepositoryDto mapToDto(GHRepository repository) {
        var pullRequests = StreamSupport.stream(repository.queryPullRequests().list().spliterator(), true)
                .map(pullRequestMapper::mapToDto)
                .toList();

        return RepositoryDto.builder()
                .id(repository.getId())
                .name(repository.getFullName())
                .ownerLogin(repository.getOwner().getLogin())
                .repositoryUrl(repository.getHtmlUrl().toString())
                .pullRequests(pullRequests)
                .pullRequestCount(pullRequests.size())
                .build();
    }
}
