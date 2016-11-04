# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# Contributors:
# Codenvy, S.A. - initial API and implementation

FROM dylanturn/docker-scatter
MAINTAINER Dylan Turnbull <dylanturn@gmail.com>

RUN cd /root; git clone https://github.com/dylanturn/Scatter.git; cd Scatter; mvn -f Scatter/pom.xml

EXPOSE 5000
WORKDIR /root/Scatter/target/
ENTRYPOINT ["java -jar scatter-1.0-jar-with-dependencies.jar"]

CMD tail -f /dev/null