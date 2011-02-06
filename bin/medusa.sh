#!/bin/ksh

. /etc/profile.d/javaenv.sh

LIB="/var/lib/medusa"

${JAVA_HOME}/bin/java -classpath "${LIB}/medusa.jar" com.sarxos.medusa.CLI "$@"

exit $?
