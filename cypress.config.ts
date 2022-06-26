import { defineConfig } from 'cypress'

export default defineConfig({
  env: {
    TAGS: 'not @ignore and not @slow',
  },
  chromeWebSecurity: false,
  screenshotOnRunFailure: true,
  pageLoadTimeout: 100000,
  defaultCommandTimeout: 6000,
  trashAssetsBeforeRuns: true,
  viewportWidth: 1000,
  viewportHeight: 660,
  environment: 'ci',
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts').default(on, config)
    },
    specPattern: 'cypress/integration/**/*.feature',
    excludeSpecPattern: [
      '**/*.{js,ts}',
      '**/__snapshots__/*',
      '**/__image_snapshots__/*',
    ],
    baseUrl: 'http://localhost:3000',
  },
})
