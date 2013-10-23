#!/bin/bash

# Determine apps home directory if it has not been specified.
if [ -z "$MAIL_HOME" -o ! -d "$MAIL_HOME" ] ; then
  PRG="$0"
  MAIL_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  MAIL_HOME=`cd "$MAIL_HOME" && pwd`
fi

BOOT_JAR="$MAIL_HOME/lib/boot.jar"

if [ ! -f "$BOOT_JAR" ] ; then
  echo "Error: MAIL_HOME is not defined correctly."
  echo "  Unable to find boot.jar"
  exit 1
fi

# Sort out the location of the java executable.
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD=`which java 2> /dev/null`
    if [ -z "$JAVACMD" ] ; then
      JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is not defined correctly."
    echo "  We cannot execute $JAVACMD"
    exit 1
fi

if [ -z "$JAVA_OPTS" ] ; then
    if [ -n "$MAIL_HOME/config/jvm.options" ] ; then
        JAVA_OPTS=`cat $MAIL_HOME/config/jvm.options`
    fi

    if [ -z "$JAVA_OPTS" ] ; then
        JAVA_OPTS="-Xmx1024m -XX:MaxPermSize=128m"
    fi
fi

BANNER="$MAIL_HOME/bin/banner.txt"

echo
cat $BANNER
echo

if [ -z "$MAIL_OUT" ] ; then
    "$JAVACMD" $JAVA_OPTS -classpath "$BOOT_JAR" -Dmail.home="$MAIL_HOME" -Djava.awt.headless=true com.zutubi.services.mail.boot.MailControl "$@"
else
    "$JAVACMD" $JAVA_OPTS -classpath "$BOOT_JAR" -Dmail.home="$MAIL_HOME" -Djava.awt.headless=true com.zutubi.services.mail.boot.MailControl "$@" >> "$MAIL_OUT" 2>&1
fi

exit $?
