#!/bin/ksh

#. /etc/profile.d/javaenv.sh

PWD=$(pwd)

MEDUSA_CLASSPATH=$CLASSPATH
MEDUSA_HOME="d:/usr/sarxos/workspace/Eclipse/Medusa"
#MEDUSA_LIB=$MEDUSA_HOME/lib
MEDUSA_LIB=lib

cd $MEDUSA_HOME

# check if Medusa lib directory exists
if [ ! -d $MEDUSA_LIB ]
then
	echo "Medusa lib directory '$MEDUSA_LIB' is missing!"
	exit 1
fi

# create classpath for Medusa
for f in $(ls $MEDUSA_LIB)
do
	MEDUSA_CLASSPATH="$MEDUSA_CLASSPATH;$MEDUSA_LIB/$f"
done

# execute Medusa CLI
${JAVA_HOME}/bin/java \
	-classpath "$MEDUSA_CLASSPATH" \
	com.sarxos.medusa.cli.CLI "$@"

cd $PWD

exit $?
