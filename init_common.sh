#!/bin/bash
# Get the currently checked out branch
jvm_branch=`git branch | grep \* | cut -d ' ' -f2`

# Check if branch exists in common
pushd ../8woc2018-common
git rev-parse --verify $jvm_branch
if [ $? -eq 0 ]; then
    # Branch exists
    git checkout $jvm_branch
else git checkout dev;
fi
popd