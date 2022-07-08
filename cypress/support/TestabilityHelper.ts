/// <reference types="cypress" />

// @ts-check

class TestabilityHelper {
  hourOfDay(days: number, hours: number) {
    return new Date(1976, 5, 1 + days, hours);
  }

  seedLink(
    cy: Cypress.cy & CyEventEmitter,
    type: string,
    fromNoteTitle: string,
    toNoteTitle: string,
  ) {
    return cy.get(`@${this.seededNoteIdMapAliasName}`).then((seededNoteIdMap) => {
      expect(seededNoteIdMap).haveOwnPropertyDescriptor(fromNoteTitle)
      expect(seededNoteIdMap).haveOwnPropertyDescriptor(toNoteTitle)
      const fromNoteId = seededNoteIdMap[fromNoteTitle]
      const toNoteId = seededNoteIdMap[toNoteTitle]
      this.postToTestabilityApiSuccessfully(cy, "link_notes", {
        body: {
          type,
          source_id: fromNoteId,
          target_id: toNoteId,
        },
      })
    })
  }
  getSeededNoteIdByTitle(cy: Cypress.cy & CyEventEmitter, noteTitle: string) {
    return cy.get(`@${this.seededNoteIdMapAliasName}`).then((seededNoteIdMap) => {
      expect(seededNoteIdMap).haveOwnPropertyDescriptor(noteTitle)
      return seededNoteIdMap[noteTitle]
    })
  }
  seedNotes(
    cy: Cypress.cy & CyEventEmitter,
    seedNotes: unknown[],
    externalIdentifier: string,
    circleName: string | null,
  ) {
    this.postToTestabilityApi(cy, "seed_notes", {
      body: {
        externalIdentifier,
        circleName,
        seedNotes,
      },
    }).then((response) => {
      expect(Object.keys(response.body).length).to.equal(seedNotes.length)
      cy.wrap(response.body).as(this.seededNoteIdMapAliasName)
    })
  }

  private get seededNoteIdMapAliasName() {
    return "seededNoteIdMap"
  }

  cleanAndReset(cy: Cypress.cy & CyEventEmitter, countdown: number) {
    this.postToTestabilityApi(cy, "clean_db_and_reset_testability_settings", {
      failOnStatusCode: countdown === 1,
    }).then((response) => {
      if (countdown > 0 && response.status !== 200) {
        this.cleanAndReset(cy, countdown - 1)
      }
    })
  }

  postToTestabilityApiSuccessfully(
    cy: Cypress.cy & CyEventEmitter,
    path: string,
    options: { body?: Record<string, unknown>; failOnStatusCode?: boolean },
  ) {
    this.postToTestabilityApi(cy, path, options).its("status").should("equal", 200)
  }

  postToTestabilityApi(
    cy: Cypress.cy & CyEventEmitter,
    path: string,
    options: { body?: Record<string, unknown>; failOnStatusCode?: boolean },
  ) {
    return cy.request({
      method: "POST",
      url: `/api/testability/${path}`,
      ...options,
    })
  }
  getTestabilityApiSuccessfully(cy: Cypress.cy & CyEventEmitter, path: string) {
    return cy.request({
      method: "GET",
      url: `/api/testability/${path}`,
    })
  }
}

export default TestabilityHelper
