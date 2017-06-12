#!/bin/bash

set -e

if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    echo "Skipping deployment for pull request"
    exit
else 
    echo "Setting security"
    echo ${GPG_SECRET_KEYS} | base64 --decode | ${GPG_EXECUTABLE} --import
    echo ${GPG_OWNERTRUST} | base64 --decode | ${GPG_EXECUTABLE} --import-ownertrust
fi

if [[ -n ${TRAVIS_TAG} ]]; then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings maven_deploy_settings.xml org.codehaus.mojo:versions-maven-plugin:2.2:set -DnewVersion=${TRAVIS_TAG} 1>/dev/null 2>/dev/null
    echo "release deploy"
    mvn -B clean deploy --settings maven_deploy_settings.xml -P release -Dfindbugs.skip=true -Djacoco.skip=true
else 
		if [[ ${TRAVIS_BRANCH} != 'master' ]]; then
		    echo "Skipping deployment for branch \"${TRAVIS_BRANCH}\""
		    exit
		fi
		mvn -B deploy --settings maven_deploy_settings.xml -DskipTests=true -Dfindbugs.skip=true -Djacoco.skip=true
fi
