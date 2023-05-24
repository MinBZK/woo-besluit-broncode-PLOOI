#!/bin/bash
# 3 params;
# 1; name of the image, pass "_none_" to skip the Docker build
# 2; name of the environment
# 3...; wepapp names (subdirs of ../xslweb/home)

if [ ! -d ${REPOS_DIR:-.}/xslweb-docker/environment/$2 ]; then
  echo "environment $2 does not exist"
  exit 1
fi
rm -rf target
mkdir -p target/home/webapps
cp -R ${REPOS_DIR:-.}/xslweb/home/config/ ${REPOS_DIR:-.}/xslweb/home/common/ target/home
cp -R ${REPOS_DIR:-.}/xslweb-docker/environment/$2/config target/home 2> /dev/null
i=0
for app in "$@"; do
  if [ $i -ge 2 ]; then
    cp -R ${REPOS_DIR:-.}/xslweb/home/webapps/${app} target/home/webapps
    cp -R ${REPOS_DIR:-.}/xslweb-docker/environment/$2/webapps/${app} target/home/webapps  2> /dev/null
  fi
  i=$((i+1))
done
if [ "$1" != "_none_" ]; then
  echo Building $1
  docker build --tag koop-plooi/$1 --tag harbor.cicd.s15m.nl/koop-plooi/$1:latest -f ${REPOS_DIR:-.}/xslweb-docker/Dockerfile target
fi
