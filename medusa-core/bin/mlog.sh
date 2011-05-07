#!/bin/ksh

# Medusa environment variables
# TODO: move to separate file, but first need some instalator
export MEDUSA_HOME="D:\usr\sarxos\workspace\Eclipse\Medusa"

cd "$MEDUSA_HOME/log"
tail -f -n 0 *.log
