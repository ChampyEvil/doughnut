module.exports = {
  root: true,
  parser: "vue-eslint-parser",
  parserOptions: {
    parser: {
      js: "espree",
      ts: "@typescript-eslint/parser",
      "<template>": "espree",
    },
    ecmaVersion: 12,
    sourceType: "module",
    createDefaultProgram: true,
    project: ["./tsconfig.json"],
    extraFileExtensions: [".vue"],
    vueFeatures: {
      filter: false,
      interpolationAsNonHTML: true,
      styleCSSVariableInjection: true,
    },
  },
  env: {
    "vue/setup-compiler-macros": true,
    browser: true,
    es2021: true,
    node: true,
    jest: true,
  },
  extends: [
    "airbnb-base",
    "eslint:recommended",
    "@vue/typescript/recommended",
    "plugin:vue/base",
    "plugin:vue/vue3-strongly-recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:testing-library/vue",
    "plugin:import/typescript",
    "plugin:import/recommended",
    "plugin:prettier/recommended",
    "prettier",
  ],
  ignorePatterns: [
    "vite.config.ts",
    "babel.config.js",
    "jest.config.js",
    "tests/setupJest.js",
  ],
  settings: {
    "import/extensions": [".ts", ".tsx"],
    "import/resolver": {
      alias: {
        map: [["@", "./src"]],
      },
    },
  },
  plugins: ["vue", "testing-library", "@typescript-eslint", "jest-dom"],
  rules: {
    "no-unused-vars": ["error", { varsIgnorePattern: ".*", args: "none" }],
    "no-param-reassign": ["error", { props: false }],
    "no-return-assign": ["error", "except-parens"],
    "no-useless-return": "off",
    "no-console": process.env.NODE_ENV === "production" ? "error" : "warn",
    "no-debugger": process.env.NODE_ENV === "production" ? "error" : "warn",
    "linebreak-style": ["error", "unix"],
    "import/extensions": "off",
    "import/no-self-import": "error",
    "import/no-cycle": ["error", { maxDepth: 1, ignoreExternal: true }],
    "testing-library/await-async-query": "error",
    "testing-library/no-await-sync-query": "error",
    "testing-library/no-debugging-utils": "warn",
    "testing-library/no-dom-import": "off",
    "vue/multi-word-component-names": 0,
  },
  overrides: [
    {
      files: ["*.json"],
      rules: {
        "no-unused-expressions": "off",
      },
    },
    {
      files: ["*.config.*", ".*.js"],
      rules: {
        "import/no-extraneous-dependencies": "off",
      },
    },
    {
      files: ["**/*.vue"],
      parser: "vue-eslint-parser",
      rules: {
        "no-undef": "off",
      },
    },
    {
      files: ["**/*.ts"],
      parser: "@typescript-eslint/parser",
      rules: {
        "no-undef": "off",
        "no-use-before-define": "off",
      },
    },
    {
      files: ["**/*.vue"],
      rules: {
        "@typescript-eslint/no-unused-vars": "off",
        "vue/require-default-prop": "off",
        "vue/v-bind-style": "off",
        "vue/no-template-shadow": "off",
      },
    },
  ],
};
