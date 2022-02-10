FROM registry.hub.docker.com/library/ubuntu:20.04

ENV DEBIAN_FRONTEND="noninteractive" \
    LANG="en_US.UTF-8" \
    LANGUAGE="en_US:en" \
    OPENEASE_HOME="/opt/openease" \
    OPENEASE_USER="ops"
ENV LC_ALL="${LANG}"

# install/update OS packages
RUN apt-get update \
 && apt-get upgrade -y \
 && apt-get install -y ca-certificates \
                       locales \
#                       net-tools \
#                       iputils-ping \
#                       telnet \
                       wget \
#                       curl \
#                       zip \
#                       python3 \
 && echo "${LANG} UTF-8" >> /etc/locale.gen \
 && locale-gen ${LANG} \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/* \
           /tmp/* \
           /var/tmp/*

# create OPENEASE_USER user, group, and directories
RUN addgroup --gid 1234 ${OPENEASE_USER} \
 && adduser --uid 1234 \
            --ingroup ${OPENEASE_USER} \
            --home ${OPENEASE_HOME} \
            --gecos '' \
            --disabled-password \
            ${OPENEASE_USER}

# switch to OPENEASE_USER
USER ${OPENEASE_USER}

WORKDIR ${OPENEASE_HOME}

RUN mkdir -p ${OPENEASE_HOME}/logs

CMD [ "/bin/bash" ]
