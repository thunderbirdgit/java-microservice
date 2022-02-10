FROM @docker.registry@/@docker.repo@/@docker.groupId@/base:@project.version@

USER root

### install Java
# set version
ARG JAVA_VERSION_MAJOR="11"
ARG JAVA_VERSION_MINOR="0"
ARG JAVA_VERSION_PATCH="14.9.1"
ARG JAVA_VERSION_OSARCH="linux-x64"
ARG JAVA_VERSION_MD5="bc1bc7203435fb7eaca360f581af73f3"
ENV JDK_HOME="/opt/java/java" \
    JAVA_VERSION="${JAVA_VERSION_MAJOR}.${JAVA_VERSION_MINOR}.${JAVA_VERSION_PATCH}"
ARG JDK_ARCHIVE_FILENAME="amazon-corretto-${JAVA_VERSION}-${JAVA_VERSION_OSARCH}.tar.gz"
# download and unarchive
RUN mkdir -p /opt/java \
# BEGIN - OPTION 1 - without MD5 check
# && curl -jksSL "https://corretto.aws/downloads/resources/${JAVA_VERSION_MAJOR}.${JAVA_VERSION_MINOR}.${JAVA_VERSION_PATCH}/${JDK_ARCHIVE_FILENAME}" \
#  | gunzip -c - \
#  | tar -xf - -C /opt/java \
# END - OPTION 1 - without MD5 check
# BEGIN - OPTION 2 - with MD5 check
 && cd /opt/java \
 && wget "https://corretto.aws/downloads/resources/${JAVA_VERSION_MAJOR}.${JAVA_VERSION_MINOR}.${JAVA_VERSION_PATCH}/${JDK_ARCHIVE_FILENAME}" \
 && echo "${JAVA_VERSION_MD5} ${JDK_ARCHIVE_FILENAME}" | md5sum -c \
 && tar -xzf ${JDK_ARCHIVE_FILENAME} \
 && rm ${JDK_ARCHIVE_FILENAME} \
# END - OPTION 2 - with MD5 check
 && ln -s /opt/java/amazon-corretto-${JAVA_VERSION}-${JAVA_VERSION_OSARCH} ${JDK_HOME} \
# remove unwanted files
 && rm -rf ${JDK_HOME}/ADDITIONAL_LICENSE_INFO \
           ${JDK_HOME}/ASSEMBLY_EXCEPTION \
           ${JDK_HOME}/LICENSE \
           ${JDK_HOME}/README.md \
           ${JDK_HOME}/version.txt \
           ${JDK_HOME}/lib/*src.zip \
 && find ${JDK_HOME}/ -name '*appletviewer*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*extcheck*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*idlj*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jarsigner*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*javah*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*javap*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jconsole*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jdmpview*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jdb*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jhat*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jjs*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jmap*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jrunscript*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jstack*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jstat*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*native2ascii*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*orbd*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*policytool*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*rmic*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*tnameserv*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*schemagen*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*serialver*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*servertool*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*tnameserv*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*traceformat*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*wsgen*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*wsimport*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*xjc*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.activation.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.corba.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.transaction.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.xml.ws.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.xml.ws.annotation.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.desktop.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.datatransfer.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jdk.scripting.nashorn.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jdk.scripting.nashorn.shell.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jdk.jconsole.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.scripting.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.se.ee.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.se.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.sql.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*java.sql.rowset.jmod*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*ct.sym*' | xargs -r rm -rf \
 && find ${JDK_HOME}/ -name '*jexec*' | xargs -r rm -rf \
 && rm -rf /var/cache/apk/*

USER ${OPENEASE_USER}
