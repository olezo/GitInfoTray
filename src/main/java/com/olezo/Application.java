package com.olezo;

import com.olezo.dto.PullRequestDto;
import com.olezo.gui.Gui;
import com.olezo.service.GitHubService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
class Application {
    private static final int UPDATE_PERIOD = 30;
    private static final int INITIAL_DELAY = 0;

    private final Gui gui = Gui.getInstance();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final GitHubService gitHubService = new GitHubService();

    @SneakyThrows
    void run() {
        gui.displayLoadingTrayMenu();

        log.info("Scheduling tray menu update...");

        scheduler.scheduleAtFixedRate(this::updateTrayMenuAndDisplayMessages, INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.SECONDS);

        log.info("Tray menu update was scheduled");
    }

    @SneakyThrows
    private void updateTrayMenuAndDisplayMessages() {
        updateTrayMenu();

        var newPullRequests = gitHubService.getNewPullRequests();

        displayNewPullRequestsMessages(newPullRequests);
    }

    @SneakyThrows
    private void updateTrayMenu() {
        var repositoryDescriptions = gitHubService.getRepositories();
        var login = gitHubService.getLogin();

        gui.updateTrayMenu(login, repositoryDescriptions);
    }

    private void displayNewPullRequestsMessages(List<PullRequestDto> newPullRequests) {
        newPullRequests.forEach(pullRequest -> {
            var title = "New PR in " + pullRequest.getRepositoryName();

            gui.displayMessage(title, pullRequest.getTitle());
        });
    }
}
