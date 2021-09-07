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
downloadable artefact.

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
