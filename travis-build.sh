#!/bin/bash
set -e
./gradlew clean assemble

EXIT_STATUS=0
echo "Publishing archives for branch $TRAVIS_BRANCH"
if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then

  echo "Publishing archives"

  if [[ -n $TRAVIS_TAG ]]; then
      ./gradlew bintrayUpload || EXIT_STATUS=$?
      if [[ $EXIT_STATUS == 0 ]]; then
        ./gradlew publishPlugins -Dgradle.publish.key="$GRADLE_PUBLISH_KEY" -Dgradle.publish.secret="$GRADLE_PUBLISH_SECRET" || EXIT_STATUS=$?
      fi
  fi

fi

exit $EXIT_STATUS