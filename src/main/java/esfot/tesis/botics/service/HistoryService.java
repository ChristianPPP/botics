package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.History;

public interface HistoryService {
    History getActualAssigment(Long computerReference);
    void saveHistory(History history);
}
