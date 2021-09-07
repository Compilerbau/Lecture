# Contributing to this project

One of the best things about writing and publishing a lecture online is that
people like you have the opportunity to give us feedback and ask questions or
point out typos or find other errors or ambiguities in the text.

## Questions, Bugs or Feature Requests

You are welcome to open a new issue. After clicking on the "new issue" button,
you will be offered a few issue templates. Please choose the appropriate template
or select "Open a blank issue, if the templates do not fit.

Please check first if your request is already addressed in other (open or closed)
issues. Feel free to reopen matching closed issues for this purpose to enter your
request there.

## Pull Requests

You are welcome to contribute via a pull request.

Please open an issue first so that your contribution can be discussed beforehand.
Then create a new feature branch starting from the `master` branch and submit
your contribution to it with a pull request from your feature branch to the
`master` branch in this repo. Please make sure that your feature branch starts
from the top of the current `master` branch.

Except for team members who can create a new feature branch directly in this
repo, you will need to fork the repo and work on your contribution in a feature
branch there.

Please note that after acceptance (i.e. merging the feature branch into the
`master` branch) your contribution is automatically subject to the licence of
this repository (CC BY-SA 4.0).

## Building Stuff

With the GitHub workflows defined in this repo, all slide sets (PDF files) and
the web pages are automatically generated and the GitHub page is updated when
changes are made on the `master` branch. The slide sets will be provided as a
downloadable artefact to team members.

In a pull request, the name of the feature branch is used to determine the
corresponding slide set and automatically create a PDF file when changes are made
on this feature branch. This requires the feature branch to be named exactly like
the page to be compiled as a slide set.
Example: The lesson `wuppie/fluppie/foo/index.md` in the folder `markdown/` would
have `wuppie/fluppie/foo` as name for the feature branch. This string must also
be added to the variable `SRC` in the [Makefile](Makefile).
The slide set will be available as a downloadable artefact.

The workflows can also be triggered manually for team members on any branches.
Please keep in mind that the GitHub page and thus the material for the students
is also updated when the web pages are created. It would probably be a good idea
to run tests in a fork or locally :-)

### Prerequisites (for lokal builds)

You have the choice between the easy route (Docker) and the scenic route
(native installation of the tools). In both cases you will need
[GNU Make](https://www.gnu.org/software/make/) and a terminal with a shell.

You also need to clone the repository locally. Please be aware that this
repository uses some Git submodules that need to be initialised correctly in
your working copy. The easiest way to clone this repo is to clone recursively,
i.e. `git clone --recurse-submodules <repo-url> <working copy name>`.
(You can also initialise the Git submodules in your working copy with
`git submodule init && git submodule update`.)

You will probably have the best experience if you work with a unixoid operating
system, even though the Makefile and Docker should also work under Windows.

#### Easy Route: Use Docker

Install [Docker] (https://www.docker.com/) and then create the Docker image with
`make create-docker-image`.

The image is based on [Pandoc Dockerfiles](https://github.com/pandoc/dockerfiles)
(specifically `pandoc/latex`), which is downloaded first. So you need an internet
connection and several hundred megabytes will be downloaded. The image itself ends
up being about 800 megabytes.

All Make targets use this Docker image by default.

#### Scenic Route: Install and maintain all tools locally

You will need the following tools in a current version:

*   [Pandoc](https://github.com/jgm/pandoc)
*   [TeX Live](http://tug.org/texlive/)
*   [Beamer](https://github.com/josephwright/beamer)
*   [Metropolis](https://github.com/matze/mtheme)
*   Several LaTeX packages: See
    [`.github/actions/alpine-pandoc-hugo/install-packages.sh`](.github/actions/alpine-pandoc-hugo/install-packages.sh)
*   [Hugo](https://github.com/gohugoio/hugo)

To use these natively installed tools in the Make targets, set the environment
variable `DOCKER=false`. Example: `export DOCKER=false; make TARGET`.

