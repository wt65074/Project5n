#!/bin/bash

for x in {1..10}
do
  /usr/bin/time $@
done