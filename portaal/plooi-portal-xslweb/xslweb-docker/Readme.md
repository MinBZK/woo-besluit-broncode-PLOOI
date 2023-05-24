
# XSLWeb in Docker

Running XSLWeb in Docker.

The scripts in here can create containers running (subsets of) XSLWeb applications.

## What is in here, how does all of this work?

Complicating factor in running XSLWeb in a container is that environment specific configuration is
maintained in XML files which are part of the source tree. Configuration parameters like Solr endpoints
cannot be passed to the container via environment variables (which is the standard practice in container
world).

All XSLWeb applications' sources are found in ../xslweb/home sub-directories. Names of these
sub-directories are passed as parameters to the build_docker.sh script, which copies them to a build
directory. Configuration files in these sub-directories refer some default location, like the Solvinity
test environment. So building a container with those configurations will produce an application that
(probably) fails to run when deploying it on for example the Logius standaardplatform (s15m).

To fix that, the build_docker.sh script overlays the configurations with environment specific versions
which are stored in the environment directory. The name of a sub-directory in there is passed to
build_docker.sh via the first parameter.

Finally, besides the build_docker.sh script there are convenience build_*.sh scripts, which produce
containers for specific XSLWeb applications. E.g. build_plooi.sh bundles the plooi and sru applications.

## Requirements to run this

Install Docker and (optionally) Docker Compose on your computer.

On a Mac with Homebrew and Cask that's easy: `brew cask install docker`. On Windows, I don't know.

## Instructions

* `docker login https://SSSSSSSSSSSSSSSSSS`
* Build the docker image by running a build_* script and pass an environment,
  e.g. `./build_plooi.sh localhost` (or in Windows, just type the command that's in the script).
  This builds a container image from the Dockerfile.
* Set environment variables referred to in `docker-compose.yl`
  * `DCN_FRBR_REPOS_DIR` - your local FRBR repository location 
  * You can also mount the `../xslweb/home` directory in you locally running container, so you don't have to
    rebuild the container for every change. To do that uncomment the last line in docker-compose.yml,
    adding a volume and set the environment variable `LOCAL_XSLWEB_DIR` to point to the directory that
    contains your XSLWeb applications (../xslweb/home). This variable is referred to in the Docker Compose
    configuration file `docker-compose.yml`
* Run `docker-compose up`, and it should just work, connecting to a local Solr instance.
  If you want to run it in the background, run `docker-compose up -d`

The PLOOI portal will now run on http://localhost:9090 

When setting `LOCAL_XSLWEB_DIR`, the app will take the `webapp.xml` configs that are in `$LOCAL_XSLWEB_DIR/webapps/ROOT` for the PLOOI portal, and`$LOCAL_XSWLWEB_DIR/webapps/sru`. The PLOOI portal then runs against the SRU app running in the same container. The SRU app connects to a Solr endpoint, which by defaults points to a Solr instance running on your own computer. You can change the parameter, for instance to point to the SP test Solr installation. 

Some tips:
* `docker ps` will show you the running containers, including their id's
* If you want to log into a container do this: `docker exec -it xslweb bash`
* If you run in background, and want to tail the log, do this: `docker logs xslweb --tail 1000 -f`

## CI/CD on Gitlab (standaard platform)

The build_docker.sh scripts mentioned above is also used by the Gitlab pipeline (.gitlab-ci.yml).
Because we pass "_none_" as image name, the Docker build is skipped however. Instead Gitlab builds and
uploads the image using kaniko.

### TODO

* The deploy stage does not yet force a deploy when the container changes. Need to check how to do that.
* On the other hand, then it will deploy (to test) after every build. Do we want that?
* We can reuse the Gitlab by defining variables in te UI and using these in the pipeline; image name
  (plooi-portal-xslweb), modules to build (plooi sru cb-common) and K8s yaml (deployment.yaml, to be
  renamed).
