package com.olezo.gui;

import com.olezo.dto.PullRequestDto;
import com.olezo.dto.RepositoryDto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PullRequestMenuItem {
    private static final String SEPARATOR_LABEL = "-";
    private static final String OPEN_REPOSITORY_LABEL = "Open repository";

    public MenuItem getItem(RepositoryDto repository) {
        var repositoryName = getRepositoryName(repository);
        var repositoryMenu = new Menu(repositoryName);

        MenuItem openRepositoryLabel = new MenuItem(OPEN_REPOSITORY_LABEL);
        openRepositoryLabel.addActionListener(event ->
                BrowserUtil.open(repository.getRepositoryUrl())
        );

        repositoryMenu.add(openRepositoryLabel);

        createPullRequestMenuItems(repository).forEach(repositoryMenu::add);

        return repositoryMenu;
    }

    private String getRepositoryName(RepositoryDto repository) {
        return repository.getPullRequestCount() > 0
                ? String.format("(%d) %s", repository.getPullRequestCount(), repository.getName())
                : repository.getName();
    }

    private List<MenuItem> createPullRequestMenuItems(RepositoryDto repository) {
        if (repository.getPullRequestCount() == 0) {
            return List.of();
        }

        var pullRequestMeuItems = new ArrayList<MenuItem>();
        pullRequestMeuItems.add(new MenuItem(SEPARATOR_LABEL));

        repository.getPullRequests().stream()
                .map(this::createPullRequestMenuItem)
                .forEach(pullRequestMeuItems::add);

        return pullRequestMeuItems;
    }

    private MenuItem createPullRequestMenuItem(PullRequestDto pullRequest) {
        var label = String.format("%s | %s -> %s",
                pullRequest.getTitle(), pullRequest.getSourceBranch(), pullRequest.getTargetBranch());
        var pullRequestMenuItem = new MenuItem(label);
        pullRequestMenuItem.addActionListener(event -> BrowserUtil.open(pullRequest.getUrl()));

        return pullRequestMenuItem;
    }
}
