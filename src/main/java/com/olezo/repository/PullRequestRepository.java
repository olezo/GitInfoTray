package com.olezo.repository;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PullRequestRepository {
    private final Set<Long> storedPullRequestIds = new HashSet<>();

    public Set<Long> getStoredPullRequestIds() {
        log.trace("Retrieving PR ids: {}", storedPullRequestIds);

        return Collections.unmodifiableSet(storedPullRequestIds);
    }

    public void addPullRequestsIds(Set<Long> ids) {
        log.trace("Adding new PR ids: {}. Existing: {}", ids, storedPullRequestIds);

        storedPullRequestIds.addAll(ids);
    }
}
