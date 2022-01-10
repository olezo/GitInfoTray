package com.olezo.gui;

import com.olezo.dto.RepositoryDto;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class TrayPopupMenu {
    private static final String CLOSE_LABEL = "Close";
    private static final String ACCOUNTS_LABEL = "Accounts";
    private static final String NOTIFICATIONS_LABEL = "Notifications";

    private static final String NOTIFICATIONS_LINK = "https://github.com/notifications";
    private static final String GITHUB_LINK = "https://github.com/";

    private final RepositoryMenu repositoryMenu = new RepositoryMenu();

    public PopupMenu getMenu(String login, List<RepositoryDto> repositories) {
        var accountItem = new MenuItem(login);
        accountItem.addActionListener(event -> BrowserUtil.open(GITHUB_LINK + login));

        var notificationsItem = new MenuItem(NOTIFICATIONS_LABEL);
        notificationsItem.addActionListener(event -> BrowserUtil.open(NOTIFICATIONS_LINK));

        var accountsMenu = buildAccountsMenu(repositories);

        var closeItem = new MenuItem(CLOSE_LABEL);
        closeItem.addActionListener(event -> System.exit(0));

        var popup = new PopupMenu();
        popup.add(accountItem);
        popup.addSeparator();
        popup.add(notificationsItem);
        popup.add(accountsMenu);
        popup.add(closeItem);

        return popup;
    }

    private Menu buildAccountsMenu(List<RepositoryDto> repositories) {
        var accountsMenu = new Menu(ACCOUNTS_LABEL);

        var partitionedAccountsToRepositoriesMap = repositories.stream()
                .collect(Collectors.partitioningBy(
                        repository -> repository.getPullRequestCount() > 0,
                        Collectors.groupingBy(RepositoryDto::getOwnerLogin)
                ));

        var accountWithPulRequestsToRepositoriesMap = partitionedAccountsToRepositoriesMap.get(Boolean.TRUE);
        var accountWithoutPulRequestsToRepositoriesMap = partitionedAccountsToRepositoriesMap.get(Boolean.FALSE);

        var accountsWithPullRequestsMenus = buildAccountMenuItems(accountWithPulRequestsToRepositoriesMap);
        var accountsWithoutPullRequestsMenus = buildAccountMenuItems(accountWithoutPulRequestsToRepositoriesMap);

        accountsWithPullRequestsMenus.forEach(accountsMenu::add);
        accountsMenu.addSeparator();
        accountsWithoutPullRequestsMenus.forEach(accountsMenu::add);

        return accountsMenu;
    }

    private List<Menu> buildAccountMenuItems(Map<String, List<RepositoryDto>> accountToRepositoryMap) {
        return accountToRepositoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map((entry) -> {
                    var username = entry.getKey();
                    var accountMenuItem = new Menu(username);

                    entry.getValue().stream()
                            .map(repositoryMenu::getItem)
                            .forEach(accountMenuItem::add);

                    return accountMenuItem;
                })
                .toList();
    }
}
