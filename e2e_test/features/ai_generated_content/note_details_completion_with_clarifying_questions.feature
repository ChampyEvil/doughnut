Feature: AI Asks Clarifying Questions When Auto-Generating Note Details
  To obtain better auto-generated note details, I want to answer clarifying questions from the AI.

  Background:
    Given I am logged in as an existing user
    And there are some notes for the current user:
      | topicConstructor | details     |
      | Sports           | Football is |
    And OpenAI service can create thread and run with id "thread-111" when requested

  @usingMockedOpenAiService
  Scenario Outline: Responding to AI's Clarification Question
    Given the OpenAI assistant in thread "thread-111" is set to:
      | response                   | arguments                                           |
      | ask clarification question | Do you mean American Football or European Football? |
      | complete note details      | " originated from England."                         |
    When I request to complete the details for the note "Sports"
    And I <respond> to the clarifying question "Do you mean American Football or European Football?"
    Then the note details on the current page should be "<note details>"

    Examples:
      | respond               | note details                         |
      | answer "European"     | Football is originated from England. |
      | respond with "cancel" | Football is                          |

  @usingMockedOpenAiService
  Scenario: Managing Extended Clarification Dialogue
    Given the OpenAI assistant in thread "thread-111" is set to:
      | response                   | arguments                                           |
      | ask clarification question | Do you mean American Football or European Football? |
      | ask clarification question | Do you mean the American version?                   |
    When I request to complete the details for the note "Sports"
    And I answer "Ameriland" to the clarifying question "Do you mean American Football or European Football?"
    Then I should see a follow-up question "Do you mean the American version?"
    And the initial clarifying question with the response "Ameriland" should be visible
