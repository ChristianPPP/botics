package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.History;
import esfot.tesis.botics.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService{
    @Autowired
    HistoryRepository historyRepository;

    @Override
    public History getActualAssigment(Long computerReference) {
        return historyRepository.getHistoryByStateAndComputerReference(computerReference);
    }

    @Override
    public void saveHistory(History history) {
        historyRepository.save(history);
    }

    @Override
    public List<History> getAllHistories() {
        return historyRepository.findAll();
    }
}
