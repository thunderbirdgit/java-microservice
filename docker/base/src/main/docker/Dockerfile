FROM registry.hub.docker.com/phusion/baseimage:focal-1.0.0

ENV DEBIAN_FRONTEND="noninteractive" \
    LANG="en_US.UTF-8" \
    LANGUAGE="en_US:en" \
    OPENEASE_HOME="/opt/openease" \
    OPENEASE_USER="ops"
ENV LC_ALL="${LANG}"

# Install/update OS packages
RUN apt-get update \
 && apt-get upgrade -y \
 && apt-get install -y ca-certificates \
                       locales \
                       wget \
#                       curl \
#                       zip \
#                       net-tools \
#                       iputils-ping \
#                       telnet \
#                       python3 \
 && echo "${LANG} UTF-8" >> /etc/locale.gen \
 && locale-gen ${LANG} \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/* \
           /tmp/* \
           /var/tmp/*

# Add "ops" user, group, and directories
RUN addgroup --gid 1234 ${OPENEASE_USER} \
 && adduser --uid 1234 \
            --ingroup ${OPENEASE_USER} \
            --home ${OPENEASE_HOME} \
            --gecos '' \
            --disabled-password \
            ${OPENEASE_USER}

USER ${OPENEASE_USER}

WORKDIR ${OPENEASE_HOME}

RUN mkdir -p ${OPENEASE_HOME}/logs

CMD [ "/bin/bash" ]
