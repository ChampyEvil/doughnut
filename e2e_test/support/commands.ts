// ***********************************************
// custom commands and overwrite existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

/// <reference types="cypress" />
// @ts-check
import "@testing-library/cypress/add-commands"
import "cypress-file-upload"
import start from "../start"
import "./string.extensions"

Cypress.Commands.add("pageIsNotLoading", () => {
  cy.get(".loading-bar").should("not.exist")
})

Cypress.Commands.add("loginAs", (username) => {
  const password = "password"
  const token = btoa(`${username}:${password}`)
  cy.request({
    method: "GET",
    url: "/api/healthcheck",
    headers: {
      Authorization: `Basic ${token}`,
    },
  }).then((response) => {
    expect(response.status).to.equal(200)
  })
})

Cypress.Commands.add("logout", () => {
  cy.pageIsNotLoading()
  cy.request({
    method: "POST",
    url: "/logout",
  }).then((response) => {
    expect(response.status).to.equal(204)
  })
})

Cypress.Commands.add("dialogDisappeared", () => {
  cy.get(".modal-body").should("not.exist")
})

Cypress.Commands.add("findUserSettingsButton", (userName: string) => {
  cy.openSidebar()
  cy.findByRole("button", { name: "User actions" }).click()
  cy.findByRole("button", { name: `Settings for ${userName}` })
})

Cypress.Commands.add("expectBreadcrumb", (items: string) => {
  cy.get(".breadcrumb").within(() =>
    items.commonSenseSplit(", ").forEach((noteTopic: string) => cy.findByText(noteTopic)),
  )
})

Cypress.Commands.add("clearFocusedText", () => {
  // cy.clear for now is an alias of cy.type('{selectall}{backspace}')
  // it doesn't clear the text sometimes.
  // Invoking it twice seems to solve the problem.
  cy.focused().clear().clear()
})

Cypress.Commands.add("replaceFocusedTextAndEnter", (text) => {
  cy.clearFocusedText().type(text).type("{shift}{enter}")
})

Cypress.Commands.add("inPlaceEdit", (noteAttributes) => {
  for (const propName in noteAttributes) {
    const value = noteAttributes[propName]
    if (value) {
      cy.findByRole(propName.toLowerCase()).click()
      cy.replaceFocusedTextAndEnter(value)
    }
  }
})

Cypress.Commands.add(
  "fieldShouldHaveValue",
  { prevSubject: true },
  ($input: JQuery<HTMLElement>, value: string) => {
    cy.wrap($input).should("have.value", value)
  },
)

Cypress.Commands.add(
  "assignFieldValue",
  { prevSubject: true },
  ($input: JQuery<HTMLElement>, value: string) => {
    if ($input.attr("type") === "file") {
      cy.fixture(value).then((img) => {
        cy.wrap($input).attachFile({
          fileContent: Cypress.Blob.base64StringToBlob(img),
          fileName: value,
          mimeType: "image/png",
        })
      })
    } else if ($input.attr("role") === "radiogroup") {
      cy.clickRadioByLabel(value)
    } else if ($input.attr("role") === "button") {
      cy.wrap($input).click()
      cy.clickRadioByLabel(value)
    } else {
      cy.wrap($input).clear().type(value)
    }
  },
)

Cypress.Commands.add("clickRadioByLabel", (labelText) => {
  cy.findByText(labelText, { selector: "label" }).click({ force: true })
})

Cypress.Commands.add("expectNoteCards", (expectedCards: string[]) => {
  cy.get("a.card-title").should("have.length", expectedCards.length)
  expectedCards.forEach((elem) => {
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
    for (const propName in elem as any) {
      if (propName === "note-topic") {
        cy.findCardTitle(elem[propName] as string)
      } else {
        cy.findByText(elem[propName] as string)
      }
    }
  })
})

Cypress.Commands.add("routerPush", (fallback, name, params) => {
  cy.get("@firstVisited").then((firstVisited) => {
    cy.window().then(async (win) => {
      if (!!win.router && firstVisited === "yes") {
        const failed = await win.router.push({
          name,
          params,
          query: { time: Date.now() }, // make sure the route re-render
        })
        if (!failed) {
          cy.dialogDisappeared()
          return
        }
        cy.log("router push failed")
        cy.log(failed)
      }
      cy.wrap("yes").as("firstVisited")
      cy.visit(fallback)
    })
  })
})

Cypress.Commands.add("clickButtonOnCardBody", (noteTopic, buttonTitle) => {
  cy.findCardTitle(noteTopic).then(($card) => {
    cy.wrap($card)
      .parent()
      .parent()
      .findByText(buttonTitle)
      .then(($button) => {
        cy.wrap($button).click()
      })
  })
})

Cypress.Commands.add("startSearching", () => {
  start.assumeNotePage().toolbarButton("search note").click()
})

Cypress.Commands.add("expectExactLinkTargets", (targets) => {
  cy.get(".search-result .card-title a")
    .then((elms) => Cypress._.map(elms, "innerText"))
    .should("deep.equal", targets)
})

Cypress.Commands.add("clickLinkNob", (target: string) => {
  cy.findByText(target).siblings(".link-nob").click()
})

Cypress.Commands.add("changeLinkType", (targetTitle: string, linkType: string) => {
  cy.clickLinkNob(targetTitle)
  cy.clickRadioByLabel(linkType)
  cy.pageIsNotLoading()
  cy.findAllByRole("button", { name: linkType }).should("be.visible")
})

Cypress.Commands.add("findNoteCardButton", (noteTopic, btnTextOrTitle) => {
  return cy
    .findCardTitle(noteTopic)
    .parent()
    .parent()
    .parent()
    .parent()
    .findByRole("button", { name: btnTextOrTitle })
})

Cypress.Commands.add(
  "initialReviewOneNoteIfThereIs",
  ({ review_type, topic, additional_info, skip }) => {
    if (review_type == "initial done") {
      cy.findByText("You have achieved your daily new notes goal.").should("be.visible")
    } else {
      cy.findByText(topic)
      switch (review_type) {
        case "single note": {
          if (additional_info) {
            cy.get(".note-details").should("contain", additional_info)
          }
          break
        }

        case "picture note": {
          if (additional_info) {
            const [expectedDetails, expectedPicture] = additional_info.commonSenseSplit("; ")
            cy.get(".note-details").should("contain", expectedDetails)
            cy.get("#note-picture")
              .find("img")
              .should("have.attr", "src")
              .should("include", expectedPicture)
          }
          break
        }

        case "link": {
          if (additional_info) {
            const [linkType, targetNote] = additional_info.commonSenseSplit("; ")
            cy.findByText(topic)
            cy.findByText(targetNote)
            cy.get(".badge").contains(linkType)
          }
          break
        }

        default:
          expect(review_type).equal("a known review page type")
      }
      if (skip === "yes") {
        cy.findByText("Skip repetition").click()
        cy.findByRole("button", { name: "OK" }).click()
      } else {
        cy.findByText("Keep for repetition").click()
      }
    }
  },
)

Cypress.Commands.add("findCardTitle", (topic) => cy.findByText(topic, { selector: "a.card-title" }))

Cypress.Commands.add("yesIRemember", () => {
  cy.findByRole("button", { name: "Yes, I remember" })
  cy.tick(11 * 1000).then(() => {
    cy.findByRole("button", { name: "Yes, I remember" }).click({})
  })
})

Cypress.Commands.add("openSidebar", () => {
  start.routerToNotebooksPage()
  cy.pageIsNotLoading()
  cy.findByRole("button", { name: "open sidebar" }).click({ force: true })
})

Cypress.Commands.add("routerToInitialReview", () => {
  cy.routerPush("/reviews/initial", "initial", {})
})

Cypress.Commands.add("routerToRoot", () => {
  cy.routerPush("/", "root", {})
})

Cypress.Commands.add("routerToReviews", () => {
  cy.routerToRoot()
  cy.routerPush("/reviews", "reviews", {})
})

Cypress.Commands.add("routerToRepeatReview", () => {
  cy.routerPush("/reviews/repeat", "repeat", {})
})

Cypress.Commands.add("initialReviewInSequence", (reviews) => {
  cy.routerToInitialReview()
  reviews.forEach((initialReview: string) => {
    cy.initialReviewOneNoteIfThereIs(initialReview)
  })
})

Cypress.Commands.add("initialReviewNotes", (noteTopics: string) => {
  cy.initialReviewInSequence(
    noteTopics.commonSenseSplit(", ").map((topic: string) => {
      return {
        review_type: topic === "end" ? "initial done" : "single note",
        topic,
      }
    }),
  )
})

Cypress.Commands.add("repeatReviewNotes", (noteTopics: string) => {
  noteTopics.commonSenseSplit(",").forEach((topic) => {
    if (topic == "end") {
      cy.findByText("You have finished all repetitions for this half a day!").should("be.visible")
    } else {
      cy.findByText(topic, { selector: "h2" })
      cy.yesIRemember()
    }
  })
})

Cypress.Commands.add("goAndRepeatReviewNotes", (noteTopics: string) => {
  if (noteTopics.trim() === "") return
  cy.routerToRepeatReview()
  cy.repeatReviewNotes(noteTopics)
})

Cypress.Commands.add("repeatMore", () => {
  cy.routerToRepeatReview()
  cy.findByRole("button", { name: "Load more from next 3 days" }).click()
})

Cypress.Commands.add("shouldSeeQuizWithOptions", (questionParts: string[], options: string) => {
  questionParts.forEach((part) => {
    cy.get(".quiz-instruction").contains(part)
  })
  options
    .commonSenseSplit(",")
    .forEach((option: string) => cy.findByText(option).should("be.visible"))
})

Cypress.Commands.add("formField", (label) => {
  return cy.findByLabelText(label)
})

Cypress.Commands.add("subscribeToNotebook", (notebookTitle: string, dailyLearningCount: string) => {
  cy.findNoteCardButton(notebookTitle, "Add to my learning").click()
  cy.get("#subscription-dailyTargetOfNewNotes").clear().type(dailyLearningCount)
  cy.findByRole("button", { name: "Submit" }).click()
})

Cypress.Commands.add("unsubscribeFromNotebook", (noteTopic) => {
  start.routerToNotebooksPage()
  cy.findNoteCardButton(noteTopic, "Unsubscribe").click()
})

Cypress.Commands.add("searchNote", (searchKey: string, options: string[]) => {
  options?.forEach((option: string) => cy.formField(option).check())
  cy.findByPlaceholderText("Search").clear().type(searchKey)
  cy.tick(500)
})

Cypress.Commands.add("failure", () => {
  throw new Error("Deliberate CYPRESS test Failure!!!")
})

Cypress.Commands.add("undoLast", (undoType: string) => {
  cy.findByTitle(`undo ${undoType}`).click()
  cy.pageIsNotLoading()
})

Cypress.Commands.add("deleteNoteViaAPI", { prevSubject: true }, (subject) => {
  cy.request({
    method: "POST",
    url: `/api/notes/${subject}/delete`,
  }).then((response) => {
    expect(response.status).to.equal(200)
  })
})

Cypress.Commands.add("noteByTitle", (noteTopic: string) => {
  return cy
    .findCardTitle(noteTopic)
    .invoke("attr", "href")
    .then(($attr) => /notes\/(\d+)/g.exec($attr)[1])
})

Cypress.Commands.add("expectFieldErrorMessage", (field: string, message: string) => {
  cy.formField(field).siblings(".error-msg").findByText(message)
})

Cypress.Commands.add("expectAMapTo", (latitude: string, longitude: string) => {
  cy.findByText(`Location: ${latitude}'N, ${longitude}'E`)
})

Cypress.Commands.add("dismissLastErrorMessage", () => {
  cy.get(".last-error-message").click()
})
