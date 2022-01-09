package com.olezo.gui;

import com.olezo.dto.RepositoryDto;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class TrayPopupMenu {
    private static final String NOTIFICATIONS_LABEL = "Notifications";
    private static final String NOTIFICATIONS_LINK = "https://github.com/notifications";
    private static final String GITHUB_LINK = "https://github.com/";
    private static final String REPOSITORIES_LABEL = "Repositories";

    public PopupMenu getMenu(String login, List<RepositoryDto> repositories) {
        var accountItem = new MenuItem(login);
        accountItem.addActionListener(event -> BrowserUtil.open(GITHUB_LINK + login));

        var notificationsItem = new MenuItem(NOTIFICATIONS_LABEL);
        notificationsItem.addActionListener(event -> BrowserUtil.open(NOTIFICATIONS_LINK));

        var repositoriesMenu = new Menu(REPOSITORIES_LABEL);
        var repositoryMenuItem = new RepositoryMenuItem();

        Map<Boolean, Map<String, List<RepositoryDto>>> positivePullRequestCountMapToOwnerUserNameMap
                = repositories.stream()
                .collect(Collectors.partitioningBy(
                        repository -> repository.getPullRequestCount() > 0,
                        Collectors.groupingBy(RepositoryDto::getOwnerLogin)
                ));

        positivePullRequestCountMapToOwnerUserNameMap.entrySet().stream()
                // PR's with positive count goes first
                .sorted(Map.Entry.comparingByKey((b1, b2) -> Boolean.compare(b2, b1)))
                .forEachOrdered(key -> {
                    key.getValue().entrySet().stream()
                            // sort by owner login
                            .sorted(Map.Entry.comparingByKey())
                            .forEachOrdered((entry) -> {
                                var ownerUserName = entry.getKey();
                                var repositoryPullRequestsMenu = new Menu(ownerUserName);

                                entry.getValue().stream()
                                        .map(repositoryMenuItem::getItem)
                                        .forEach(repositoryPullRequestsMenu::add);

                                repositoriesMenu.add(repositoryPullRequestsMenu);
                            });

                    repositoriesMenu.addSeparator();
                });

        var closeItem = new MenuItem("Close");
        closeItem.addActionListener(event -> System.exit(0));

        var popup = new PopupMenu();
        popup.add(accountItem);
        popup.addSeparator();
        popup.add(notificationsItem);
        popup.add(repositoriesMenu);
        popup.add(closeItem);

        return popup;
    }
}
