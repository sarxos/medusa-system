#!/bin/ksh


# Medusa environment variables
# TODO: move to separate file, but first need some instalator
export MEDUSA_HOME="D:\usr\sarxos\workspace\Eclipse\Medusa"


###############################################################################


if [[ $OS != "Windows_NT" ]];
then
	echo "For now Medusa can be run only on WinNT w. Cygwin installed"
	exit 1
fi

# directories

BIN_DIR="bin"
LOG_DIR="log"
LIB_DIR="lib"

EXECUTABLE="wrapper.exe"

MEDUSA_BIN="$MEDUSA_HOME\\$BIN_DIR"
MEDUSA_LIB="$MEDUSA_HOME\\$LIB_DIR"
MEDUSA_DAT="$MEDUSA_HOME\data"
NT_WRAPPER_BIN="$EXECUTABLE"
NT_WRAPPER_CFG="$MEDUSA_HOME\data\service-def.conf"
NT_WRAPPER_TMP="$MEDUSA_DAT\service.conf"

# check if Medusa lib directory exists

if [ ! -d $MEDUSA_LIB ]
then
	echo "Medusa lib directory '$MEDUSA_LIB' is missing!"
	exit 1
fi

# create service configuration

touch "$NT_WRAPPER_TMP"
cat "$NT_WRAPPER_CFG" > "$NT_WRAPPER_TMP"

# create classpath config content

echo >> "$NT_WRAPPER_TMP"
echo "#### MEDUSA GENERATED CONTENT #### BEGIN" >> "$NT_WRAPPER_TMP"
echo "wrapper.logfile=$LOG_DIR/service.log" >> "$NT_WRAPPER_TMP"
echo "wrapper.java.library.path.1=$LIB_DIR" >> "$NT_WRAPPER_TMP"
echo "wrapper.java.classpath.1=target" >> "$NT_WRAPPER_TMP"

typeset -i num=2

for f in $(ls $MEDUSA_LIB)
do
	echo "wrapper.java.classpath.$num=$LIB_DIR/$f" >> "$NT_WRAPPER_TMP"
	num=num+1
done

echo "#### MEDUSA GENERATED CONTENT #### END" >> "$NT_WRAPPER_TMP"
echo >> "$NT_WRAPPER_TMP"

cd "$MEDUSA_HOME"

ACTION=""

case $1 in
	install)
		ACTION="-i"
		;;
	remove)
		ACTION="-r"
		;;
	start)
		ACTION="-t"
		;;
	stop)
		ACTION="-p"
		;;
	restart)
		bin/medusa.sh stop
		bin/medusa.sh start
		exit $?
		;;	
	status)
		ACTION="-q"
		;;
	*)

		# create classpath for Medusa
		for f in $(ls $MEDUSA_LIB)
		do
			MEDUSA_CLASSPATH="$MEDUSA_CLASSPATH;$LIB_DIR\\$f"
		done

		MEDUSA_CLASSPATH="$MEDUSA_CLASSPATH;target"

		# execute Medusa CLI
		${JAVA_HOME}/bin/java -classpath "$MEDUSA_CLASSPATH" com.sarxos.medusa.cli.CLI "$@"
		
		exit $?
		;;
esac

./$NT_WRAPPER_BIN $ACTION "$NT_WRAPPER_TMP"

exit $?
