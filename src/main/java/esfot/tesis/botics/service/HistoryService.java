package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.History;

import java.util.List;

public interface HistoryService {
    History getActualAssigment(Long computerReference);
    void saveHistory(History history);
    List<History> getAllHistories();
}
