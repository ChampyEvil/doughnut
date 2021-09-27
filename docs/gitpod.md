# Gitpod Dev Environment in the Cloud

## Pre-requisite

- Google Chrome or Chrome/Chromium derivative browser. Ensure the browser is configured to allow new tab spawning/popup for URL pattern `[*.gitpod.io]`.
- A [Github](https://www.github.com) account (authroised for [doughnut Github](https://github.com/nerds-odd-e/doughnut) repo write access).
- A [Gitpod](https://gitpod.io/login/) account signed up using your Github account above.
- _Optional_ [Chrome extension for Gitpod](https://chrome.google.com/webstore/detail/gitpod-always-ready-to-co/dodmmooeoklaejobgleioelladacbeki). This chrome extension gives you a nice green 'Gitpod' button at the top of [doughnut Github](https://github.com/nerds-odd-e/doughnut) repo to launch your Gitpod workspace. You may also enter the URL [gitpod.io#/https://github.com/nerds-odd-e/doughnut](gitpod.io#/https://github.com/nerds-odd-e/doughnut) to achieve the same effect.
- [More details/info](https://www.gitpod.io/docs/browser-extension/) about starting up your doughnut dev env in Gitpod.

## Up & running your Gitpod `doughnut` development environment

### Basic Gitpod VSCode workspace

- Visit [doughnut Github](https://github.com/nerds-odd-e/doughnut) repo from your chrome/chromium-derivative broswer; Click on the `Gitpod` green button (near the top right corner of `doughnut`'s Github page if you have installed the chrome extension from above prerequisite step) to launch your development workspace. Or enter in your chomre browser's URL input [gitpod.io#/https://github.com/nerds-odd-e/doughnut](gitpod.io#/https://github.com/nerds-odd-e/doughnut).
- From your Gitpod VSCode workspace browser tab launched from above step, at the bottom left hand corner of the VSCode IDE, locate the `Login to Github` icon/button and perform your Github login by entering your relevant Github credentials in the new browser tab spawned.
- Once your Gitpod workspace with VSCode has launched successfully in a new chrome/chromium-derivative browser from above step, open a VSCode 'terminal'. (after VSCode browser tab is launched, another tab will launch which is a VNC connection to your gitpod for Cypress IDE launch for local development E2E testing use).

### zsh & git config to setup development workspace

- On the Gitpod workspace VSCode, start a zsh terminal.
- From Gitpod zsh terminal, configure your _git_ `user.name` and `user.email` to ensure your git commits are labeled correctly and also to get Gitpod-Github access for code push permission in place (you should already have been authorised for Github doughnut repo write access).

```bash
git config user.name "Your beautiful name"
git config user.email "your_email@your_domain.com"
```

### Get your doughnut DB tables setup

- From root of `doughnut` run `./gradlew bootRunE2E` to setup and migrate your base virgin `doughnut` DB tables via `flyway` migrations. Once the migrations have completed (read the `springboot` startup logs from the VSCode terminal), use `Ctrl-C` to exit `springboot` backend server application process. (this might take some time - once done, `Ctrl-C` to exit process on completion).

```bash
### You should see in the springboot terminal log output a line similar to the below ###
.
INFO 2142 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : Successfully applied 57 migrations to schema `doughnut_e2e_test`
.
.
```

### Preparation steps to run doughnut backend unit tests & cypress End-to-End tests

- From the root of the `doughnut` codebase (this should be on path `/workspace/doughnut`), run `yarn` to get End-to-End testing tooling setup.
- From `doughnut/frontend` path, also run `yarn` followed by `yarn build` to prepare for frontend Vue3 development tool packages setup.
- From root of `doughnut` source path, execute `yarn test:dev` to execute the full headless cypress End-to-End test suite.

### Running java springboot unit tests

- From the root of the `doughnut` codebase, run `.\gradlew test`. This assumes you have had your doughnut DB tables setup from above.

### Running frontend JS/TS Vue3 unit tests

- Navigate to `doughnut/frontend`. Execute `yarn` to install the frontend packages. Now execute `yarn test` to run the full frontend `jest` unit tests.
