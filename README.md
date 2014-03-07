# Lannister
#### An EIMassim implementation

## Project set-up

### Software to install:
- git :) - use git bash, preferably - see (https://help.github.com/articles/set-up-git)
- MSys (http://www.mingw.org/wiki/MSYS)
- Eclipse JEE (https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr2)

### Set-up sources:
- open terminal window (cmd.exe), run
```
scripts\setup_workspace.sh
```
- make sure there are no weird errors. NOTE: known warning (to be ignored): 
```
...
Download complete. Extracting...
warning [massim-2013-1.4-bin.zip]:  319 extra bytes at beginning or within zipfile
  (attempting to process anyway)
...
```
- start Eclipse, (select workspace as folder where you cloned the project, i.e. parent of "Lannister" folder), import project:
```
File -> Import... -> General/Existing Projects into Workspace -> Next -> Browse (for "Lannister" folder) -> Check "Lannister" entry in "Projects" list -> Finish
```
- run
```
massim-2013-1.4\massim\scripts\startMarsMonitor.sh
```
as result, a java window for monitoring the map will appear
- in a separate terminal window run:
```
massim-2013-1.4\massim\scripts\startServer.sh
```
- input 0, but don't press enter when console output is "Please press ENTER to start the tournament."
- in Eclipse, Run the project (Run -> Run or Ctrl+F11)
- wait for the following console output to be shown:
```
...
Entity ${CONNECTION_NAME}: authenticiation acknowledged
Entity ${CONNECTION_NAME}: connection successfully authenticated
Entity ${CONNECTION_NAME}: listening for incoming messages
...
```
- in the startServer.sh terminal press ENTER
- in the startMarsMonitor.sh-generated java window observe as one of the agents is moving
- in the Eclipse console input a vertex name, e.g. v1
- observe as the agent will go to the target node - and continue to wonder around afterwards

## Project structure:

TODO.