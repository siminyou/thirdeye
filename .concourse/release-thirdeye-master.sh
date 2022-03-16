#!/bin/bash

#
# Copyright (c) 2022 StarTree Inc. All rights reserved.
# Confidential and Proprietary Information of StarTree Inc.
#

if [ -z "$CONCOURSE_TEAM" ]; then
  echo "CONCOURSE_TEAM is undefined. Using \`thirdeye\`"
  export CONCOURSE_TEAM="thirdeye"
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )" && \
  fly \
	-t $CONCOURSE_TEAM \
	set-pipeline \
	-p release-thirdeye-master \
	-c $DIR/release-thirdeye.yml