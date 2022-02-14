# Local development machine development environment setup with nix

### 1. Install nix

We use nix to manage and ensure a reproducible development environment ([nixos.org](https://nixos.org)).

#### For macOS:

Full details on nix installation on macOS [here](https://nixos.org/manual/nix/stable/#sect-macos-installation)

```bash
 sh < (curl -k -L https://nixos.org/nix/install) --darwin-use-unencrypted-nix-store-volume
```

#### For Linux:

```bash
sh < (curl -k -L https://nixos.org/nix/install) --no-daemon
```

(NB: if the install script fails to add sourcing of `nix.sh` in `.bashrc` or `.profile`, you can do it manually `source /etc/profile.d/nix.sh`)

### 2. Setup and run doughnut for the first time (local development profile)

Launch a new terminal in your favourite shell (I highly recommend zsh).

```bash
mkdir -p ~/.config/nix
echo 'experimental-features = nix-command flakes' >> ~/.config/nix/nix.conf
. ~/.nix-profile/etc/profile.d/nix.sh
```

Clone full all-in-one doughnut codebase from Github (Microsoft Windows OS users, please clone the repo to a non-Windows mount directory)

```bash
git clone git@github.com:nerds-odd-e/doughnut.git
```

Boot up your doughnut development environment.
MySQL DB server is started and initialised on entering the `nix develop`.

```bash
cd doughnut
export NIXPKGS_ALLOW_UNFREE=1
nix develop -c $SHELL
```

All development tool commands henceforth should be run within `nix develop -c $SHELL`
Run E2E profile springboot backend server with gradle (backend app started on port 9081)

```bash
# from doughnut source root dir
yarn && yarn frontend:build
yarn sut
open http://localhost:9081
```

Run E2E profile with backend server & frontend in dev mode & Cypress IDE (frontend app on port 3000; backend app on port 9081)
For MS Windows users, you need to ensure your WSL2 Linux has `xvfb` installed. This is not managed by Nix!

```bash
# from doughnut source root dir
yarn && yarn frontend:sut
yarn sut
yarn cy:open
```

Run Dev profile springboot backend server with gradle (backend app started on port 9082)

```bash
# from doughnut source root dir
yarn && yarn frontend:build
./gradlew bootRunDev
open http://localhost:9082
```

#### IntelliJ IDEA (Community) IDE project import

Launch your IntelliJ IDE from your host OS.

#### Setup IntelliJ IDEA with JDK17 SDK

- Locate your `nix develop` installed JDK path location from the header printout on entering ` nix develop` ($JAVA_HOME is printed to stdout on entering `nix develop`).
  - e.g. On macOS this could look like `/nix/store/yrai8hf3qkkz1a7597y1hkhwi52zamcs-zulu17.30.15-ca-jdk-17.0.1/zulu-17.jdk/Contents/Home`.
- **File -> Project Structure -> Platform Settings -> SDKs -> Add JDK...**
  - Enter the full path of above (e.g. `/nix/store/yrai8hf3qkkz1a7597y1hkhwi52zamcs-zulu17.30.15-ca-jdk-17.0.1/zulu-17.jdk/Contents/Home`).
    ![Sample `nix develop` JAVA_HOME](./images/01_doughnut_nix_develop_JAVA_HOME.png "Sample nix develop JAVA_HOME")

#### Run a single targetted JUnit5 test in IntelliJ IDEA

- Setup IntelliJ in Gradle perspective -> Gradle Settings (Wrench Icon) -> Run tests with -> IntelliJ IDEA
- Locate your test file in IDE (e.g. `backend/src/test/com/odde/doughnut/controllers/NoteRestControllerTests.java`).
  - Locate specific test method to run and look out for green run arrow icon in line number gutter.
  - Click on the green run arrow icon to kick off incremental build and single test run.
