package com.odde.doughnut.models;

import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.CLOZE_SELECTION;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.DESCRIPTION_LINK_TARGET;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.FROM_DIFFERENT_PART_AS;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.FROM_SAME_PART_AS;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.LINK_SOURCE;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.LINK_TARGET;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.PICTURE_SELECTION;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.PICTURE_TITLE;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.SPELLING;
import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.WHICH_SPEC_HAS_INSTANCE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.odde.doughnut.entities.*;
import com.odde.doughnut.testability.MakeMe;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizQuestionGeneratorTest {
  @Autowired MakeMe makeMe;

  Note note;

  @BeforeEach
  void setup() {
    note = makeMe.aNote().please();
  }

  @Test
  void note() {
    makeMe.theNote(note).rememberSpelling().please();
    ReviewPoint reviewPoint = makeMe.aReviewPointFor(note).inMemoryPlease();
    List<QuizQuestionEntity.QuestionType> questionTypes = getQuestionTypes(reviewPoint);
    assertThat(
        questionTypes, contains(SPELLING, CLOZE_SELECTION, PICTURE_TITLE, PICTURE_SELECTION));
  }

  @Test
  void linkExclusive() {
    Note note2 = makeMe.aNote().linkTo(note).please();
    ReviewPoint reviewPoint = makeMe.aReviewPointFor(note2.getLinks().get(0)).inMemoryPlease();
    List<QuizQuestionEntity.QuestionType> questionTypes = getQuestionTypes(reviewPoint);
    assertThat(
        questionTypes,
        containsInAnyOrder(
            LINK_TARGET,
            LINK_SOURCE,
            WHICH_SPEC_HAS_INSTANCE,
            FROM_SAME_PART_AS,
            FROM_DIFFERENT_PART_AS,
            DESCRIPTION_LINK_TARGET));
  }

  @Test
  void notAllLinkQuestionAreAvailableToAllLinkTypes() {
    Note note2 = makeMe.aNote().linkTo(note, LinkType.RELATED_TO).please();
    ReviewPoint reviewPoint = makeMe.aReviewPointFor(note2.getLinks().get(0)).inMemoryPlease();
    List<QuizQuestionEntity.QuestionType> questionTypes = getQuestionTypes(reviewPoint);
    assertTrue(questionTypes.isEmpty());
  }

  private List<QuizQuestionEntity.QuestionType> getQuestionTypes(ReviewPoint reviewPoint) {
    return reviewPoint.availableQuestionTypes(false);
  }
}
