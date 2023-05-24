
# ubr-koop-starter

UBR KOOP starter project for a head start!


## Getting started
This project contains the files, settings and configuration for creating a archetype in maven 
##Command to execute

Be sure to be in a folder where there is no pom files already present.
```
mvn archetype:generate -DarchetypeGroupId=nl.overheid.koop 
-DarchetypeArtifactId=plooi-aanlever 
-DarchetypeVersion=0.0.1-SNAPSHOT 

-DgroupId= {{new artifact for project id eg: -DgroupId=nl.overheid.koop}
-DartifactId= {new artifact for project id eg: -DartifactId=plooi-aanlever-validate} 
-Dversion= {new artifact for project id eg: -Dversion=1.0.1-SNAPSHOT}
```
Where the last 3 commands are variables to be changed.




To make it easy for you to get started with GitLab, here's a list of recommended next steps.

## Add your files

With the release of the maven plugin 3.2.0 there is an bug/issue within the release.
The .gitignore it not proccesed and taken with in the protype generation files
Therefore it is recommened tatone must manually add the .gitignore file to the project,
( till the bug is fixed by maven)

- [ ] Add .gitignore 

## Test and Deploy

Use the built-in continuous integration in GitLab.



