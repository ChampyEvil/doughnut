package com.odde.doughnut.models.quizFacotries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.odde.doughnut.controllers.dto.QuizQuestion;
import com.odde.doughnut.entities.LinkingNote;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.factoryServices.quizFacotries.factories.ClozeLinkTargetQuizFactory;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClozeLinkTargetQuizFactoryTest {
  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note target;
  Note source;
  Note anotherSource;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote().creatorAndOwner(userModel).please();
    target = makeMe.aNote("rome").under(top).please();
    source = makeMe.aNote("Rome is not built in a day").under(top).linkTo(target).please();
    reviewPoint = makeMe.aReviewPointFor(source.getLinks().get(0)).inMemoryPlease();
    anotherSource = makeMe.aNote("pompeii").under(top).please();
    makeMe.refresh(top);
  }

  @Nested
  class WhenThereAreMoreThanOneOptions {
    @Test
    void shouldIncludeRightAnswers() {
      assertThat(
          (buildQuestion()).getStem(),
          equalTo(
              "<mark><mark title='Hidden text that is matching the answer'>[...]</mark> is not built in a day</mark> is a specialization of:"));
    }
  }

  private QuizQuestion buildQuestion() {
    return makeMe.buildAQuestion(
        new ClozeLinkTargetQuizFactory((LinkingNote) reviewPoint.getNote()), reviewPoint);
  }
}
