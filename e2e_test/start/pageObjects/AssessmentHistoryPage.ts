export const navigateToAssessmentHistory = () => {
  cy.visit('/assessment-history')
  cy.findByText('Assessment History')
  return {
    expectAssessmentHistory: (rows: Record<string, string>[]) => {
      cy.get('table tbody tr').should('have.length', rows.length)
      rows.forEach((row, index) => {
        cy.get('table tbody tr')
          .eq(index)
          .within(() => {
            cy.findByText(row['notebook topic']!).should('exist')
            cy.findByText(row.score!).should('exist')
            cy.findByText(row['total questions']!).should('exist')
          })
      })
    },
  }
}
