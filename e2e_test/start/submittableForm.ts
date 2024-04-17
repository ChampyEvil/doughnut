const submittableForm = {
  submitWith: (noteAttributes: Record<string, string | undefined>) => {
    for (const propName in noteAttributes) {
      const value = noteAttributes[propName]
      if (value) {
        cy.formField(propName).assignFieldValue(value)
      }
    }
    cy.get('input[value="Submit"]').click()
  },
  submitWithAudio: (fileName: string) => {
    cy.formField('audio').attachFile(fileName)
    cy.get('input[value="Submit"]').click()
  }
}

export default submittableForm
