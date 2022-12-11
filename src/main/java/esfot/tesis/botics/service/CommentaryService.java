package esfot.tesis.botics.service;


import esfot.tesis.botics.entity.Commentary;

import java.util.List;

public interface CommentaryService {
    List<Commentary> getCommentariesByUserId(Long idUser);
    void storeCommentary(Commentary commentary);
    List<Commentary> getCommentariesByNotUserId(Long idUser);
    Commentary getCommentaryById(Long commentaryId);
    List<Commentary> getCommentaries();
}
