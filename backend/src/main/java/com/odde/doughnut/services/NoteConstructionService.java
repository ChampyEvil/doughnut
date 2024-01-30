package com.odde.doughnut.services;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.exceptions.DuplicateWikidataIdException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.services.wikidataApis.WikidataIdWithApi;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import lombok.SneakyThrows;

public record NoteConstructionService(
    User user, Timestamp currentUTCTimestamp, ModelFactoryService modelFactoryService) {

  public Note createNoteWithWikidataInfo(
      Note parentNote,
      WikidataIdWithApi wikidataIdWithApi,
      Link.LinkType linkTypeToParent,
      String topicConstructor)
      throws DuplicateWikidataIdException, IOException, InterruptedException {
    Note note = parentNote.buildChildNote(user, currentUTCTimestamp, topicConstructor);
    modelFactoryService.save(note);
    modelFactoryService.createLink(
        note, note.getParent(), user, linkTypeToParent, currentUTCTimestamp);
    if (wikidataIdWithApi != null) {
      wikidataIdWithApi.associateNoteToWikidata(note, modelFactoryService);
      wikidataIdWithApi.getCountryOfOrigin().ifPresent(wwa -> createSubNote(note, wwa));
      wikidataIdWithApi.getAuthors().forEach(wwa -> createSubNote(note, wwa));
    }
    return note;
  }

  @SneakyThrows
  private void createSubNote(Note parentNote, WikidataIdWithApi subWikidataIdWithApi) {
    Optional<String> optionalTitle = subWikidataIdWithApi.fetchEnglishTitleFromApi();
    optionalTitle.ifPresent(
        subNoteTitle ->
            modelFactoryService
                .noteRepository
                .noteWithWikidataIdWithinNotebook(
                    parentNote.getNotebook().getId(), subWikidataIdWithApi.wikidataId())
                .stream()
                .findFirst()
                .ifPresentOrElse(
                    existingNote -> {
                      modelFactoryService.createLink(
                          parentNote,
                          existingNote,
                          user,
                          Link.LinkType.RELATED_TO,
                          currentUTCTimestamp);
                    },
                    () -> {
                      try {
                        createNoteWithWikidataInfo(
                            parentNote,
                            subWikidataIdWithApi,
                            Link.LinkType.RELATED_TO,
                            subNoteTitle);
                      } catch (Exception | DuplicateWikidataIdException e) {
                        throw new RuntimeException(e);
                      }
                    }));
  }
}
