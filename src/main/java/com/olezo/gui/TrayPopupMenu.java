package com.olezo.gui;

import com.olezo.dto.RepositoryDto;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class TrayPopupMenu {
    private static final String CLOSE_LABEL = "Close";
    private static final String REPOSITORIES_LABEL = "Repositories";
    private static final String NOTIFICATIONS_LABEL = "Notifications";

    private static final String NOTIFICATIONS_LINK = "https://github.com/notifications";
    private static final String GITHUB_LINK = "https://github.com/";

    public PopupMenu getMenu(String login, List<RepositoryDto> repositories) {
        var accountItem = new MenuItem(login);
        accountItem.addActionListener(event -> BrowserUtil.open(GITHUB_LINK + login));

        var notificationsItem = new MenuItem(NOTIFICATIONS_LABEL);
        notificationsItem.addActionListener(event -> BrowserUtil.open(NOTIFICATIONS_LINK));

        var repositoriesMenu = buildRepositoriesMenu(repositories);

        var closeItem = new MenuItem(CLOSE_LABEL);
        closeItem.addActionListener(event -> System.exit(0));

        var popup = new PopupMenu();
        popup.add(accountItem);
        popup.addSeparator();
        popup.add(notificationsItem);
        popup.add(repositoriesMenu);
        popup.add(closeItem);

        return popup;
    }

    private Menu buildRepositoriesMenu(List<RepositoryDto> repositories) {
        var repositoriesMenu = new Menu(REPOSITORIES_LABEL);
        var pullRequestMenuItem = new PullRequestMenuItem();

        var positivePullRequestCountMapToOwnerUserNameMap = repositories.stream()
                .collect(Collectors.partitioningBy(
                        repository -> repository.getPullRequestCount() > 0,
                        Collectors.groupingBy(RepositoryDto::getOwnerLogin)
                ));

        var usernameToRepositoriesWithPullRequestsMap = positivePullRequestCountMapToOwnerUserNameMap.get(Boolean.TRUE);
        var usernameToRepositoriesWithoutPullRequestsMap = positivePullRequestCountMapToOwnerUserNameMap.get(Boolean.FALSE);

        var repositoriesWithPullRequestsMenus = buildRepositoryMenuItem(pullRequestMenuItem, usernameToRepositoriesWithPullRequestsMap);
        var repositoriesWithoutPullRequestsMenus = buildRepositoryMenuItem(pullRequestMenuItem, usernameToRepositoriesWithoutPullRequestsMap);

        repositoriesWithPullRequestsMenus.forEach(repositoriesMenu::add);
        repositoriesMenu.addSeparator();
        repositoriesWithoutPullRequestsMenus.forEach(repositoriesMenu::add);

        return repositoriesMenu;
    }

    private List<Menu> buildRepositoryMenuItem(PullRequestMenuItem pullRequestMenuItem, Map<String, List<RepositoryDto>> usernameToRepositoryMap) {
        return usernameToRepositoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map((entry) -> {
                    var ownerUsername = entry.getKey();
                    var repositoryPullRequestsMenu = new Menu(ownerUsername);

                    entry.getValue().stream()
                            .map(pullRequestMenuItem::getItem)
                            .forEach(repositoryPullRequestsMenu::add);

                    return repositoryPullRequestsMenu;
                })
                .toList();
    }
}
