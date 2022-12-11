package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Commentary;
import esfot.tesis.botics.repository.CommentaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaryServiceImpl implements CommentaryService {
    @Autowired
    CommentaryRepository commentariesRepository;

    @Override
    public List<Commentary> getCommentariesByUserId(Long idUser) {
        return commentariesRepository.findAllByUserId(idUser);
    }

    @Override
    public void storeCommentary(Commentary commentary) {
        commentariesRepository.save(commentary);
    }

    @Override
    public List<Commentary> getCommentariesByNotUserId(Long idUser) {
        return commentariesRepository.findAllByUserIdNot(idUser);
    }

    @Override
    public Commentary getCommentaryById(Long commentaryId) {
        return commentariesRepository.findCommentaryById(commentaryId);
    }

    @Override
    public List<Commentary> getCommentaries() {
        return commentariesRepository.findAll();
    }
}
