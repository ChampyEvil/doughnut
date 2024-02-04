package com.odde.doughnut.models;

import com.odde.doughnut.controllers.json.SearchTerm;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.entities.repositories.NoteRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;

public class SearchTermModel {
  private final User user;
  private final SearchTerm searchTerm;
  NoteRepository noteRepository;

  public SearchTermModel(User entity, NoteRepository noteRepository, SearchTerm searchTerm) {
    this.user = entity;
    this.searchTerm = searchTerm;
    this.noteRepository = noteRepository;
  }

  private List<Note> search() {
    if (searchTerm.getAllMyCircles()) {
      return noteRepository.searchForUserInAllMyNotebooksSubscriptionsAndCircle(
          user.getId(), getPattern());
    }
    if (searchTerm.getAllMyNotebooksAndSubscriptions()) {
      return noteRepository.searchForUserInAllMyNotebooksAndSubscriptions(
          user.getId(), getPattern());
    }
    Integer notebookId = null;
    if (searchTerm.note != null) {
      notebookId = searchTerm.note.getNotebook().getId();
    }
    return noteRepository.searchInNotebook(notebookId, getPattern());
  }

  private String getPattern() {
    return "%" + searchTerm.getTrimmedSearchKey() + "%";
  }

  public List<Note> searchForNotes() {
    if (Strings.isBlank(searchTerm.getTrimmedSearchKey())) {
      return List.of();
    }

    Integer avoidNoteId = null;
    if (searchTerm.note != null) {
      avoidNoteId = searchTerm.note.getId();
    }
    Integer finalAvoidNoteId = avoidNoteId;
    return search().stream()
        .filter(n -> !n.getId().equals(finalAvoidNoteId))
        .collect(Collectors.toList());
  }
}
