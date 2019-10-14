To edit the documentation for the scala-sdk:

Edit the documentation in the .scala files of the Scaladocs branch (or any other branch that has the up-to-date scaladocs documentation)

Run `sbt doc` from the command line to generate the documentation (the docs will go to target/scala-2.12/api)

Run `git add target/scala-2.12/api && git commit -m "<commit message>"` from the command line 

Run `git subtree push --prefix target/scala-2.12/api origin gh-pages` from the command line to push the changes to the gh-pages branch

Change the meta http-equiv tag in the outermost index.html file to point the site to the sdk directory: `<meta http-equiv="Refresh" content="0; https://cognitedata.github.io/cognite-sdk-scala/com/cognite/sdk/scala/index.html" />`
