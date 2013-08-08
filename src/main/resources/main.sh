#!/bin/sh -x

# Use spring.profiles.active to switch between main site and indexer app, e.g.
# --spring.profiles.active=indexer for indexer.  Site runs in default and site profiles.

PATH=.java/bin:$PATH

if [ -f lib/newrelic*.jar ]; then
  mv lib/newrelic-agent*.jar newrelic.jar
  JAVA_AGENT=-javaagent:newrelic.jar
fi

java -XX:OnOutOfMemoryError=./.buildpack-diagnostics/killjava -Xss1M -Xmx1260m -XX:MaxPermSize=1024m ${LOCAL_OPTS} ${JAVA_AGENT} -cp . org.springframework.boot.loader.JarLauncher $*