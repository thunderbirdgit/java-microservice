FROM @docker.registry@/@docker.repo@/@docker.groupId@/java:@project.version@

LABEL groupId="@project.groupId@" \
      artifactId="@project.artifactId@" \
      version="@project.version@" \
      name="@project.name@" \
      description="@project.description@" \
      copyright="Copyright © @date.current.year@ @project.organization.name@"

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:@service.port@/info/_status || exit 1

ADD ./@project.build.finalName@-exec.@project.packaging@ ${OPENEASE_HOME}

EXPOSE @service.port@

CMD [ "/bin/sh", "-c", "${JDK_HOME}/bin/java ${JVM_OPTS} -D@environment.systemProperty@=${OPENEASE_ENV} -Dlog4j2.configurationFile=log4j2-${OPENEASE_ENV}.yaml -jar ${OPENEASE_HOME}/@project.build.finalName@-exec.@project.packaging@" ]
