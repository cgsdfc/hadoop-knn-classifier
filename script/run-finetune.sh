#!/bin/bash -xe

git pull
. ./data/iris/run-finetune.sh
. ./data/car_owners/run-finetune.sh
git add .
git commit -m "save finetune results"
git push
