#!/usr/bin/env bash

HOME_HOST=localhost
HOME_PORT=8080
HOME_URL=http://$HOME_HOST:$HOME_PORT

ETAG="\"123456789101231\""
README_URL=$HOME_URL/README.md

echo Asserting requests to $README_URL

EXPECTED="200, text/plain"
RESPONSE=$(curl --write-out '%{http_code}, %{content_type}' --silent --output /dev/null --head $README_URL)
[[ $EXPECTED = $RESPONSE ]] || echo "$README_URL expected '$EXPECTED', actual '$RESPONSE'"

EXPECTED="405, text/html"
RESPONSE=$(curl --write-out '%{http_code}, %{content_type}' --silent --output /dev/null -X POST --head $README_URL)
[[ $EXPECTED = $RESPONSE ]] || echo "$README_URL expected '$EXPECTED', actual '$RESPONSE'"

EXPECTED="404, text/html"
RESPONSE=$(curl --write-out '%{http_code}, %{content_type}' --silent --output /dev/null --head $HOME_URL/unknown)
[[ $EXPECTED = $RESPONSE ]] || echo "$HOME_URL/unknown expected '$EXPECTED', actual '$RESPONSE'"

EXPECTED="304, text/plain"
RESPONSE=$(curl --write-out '%{http_code}, %{content_type}' --silent --output /dev/null --head $README_URL --header 'If-Match: $ETAG')
[[ $EXPECTED = $RESPONSE ]] || echo "$README_URL expected '$EXPECTED', actual '$RESPONSE'"

EXPECTED="304, text/plain"
RESPONSE=$(curl --write-out '%{http_code}, %{content_type}' --silent --output /dev/null --head $README_URL --header 'If-Modified-Since: Sun Aug 30 13:35:22 CEST 2050')
[[ $EXPECTED = $RESPONSE ]] || echo "$README_URL expected '$EXPECTED', actual '$RESPONSE'"

echo Completed
