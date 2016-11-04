# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# Contributors:
# Codenvy, S.A. - initial API and implementation

FROM centos:centos7
MAINTAINER Dylan Turnbull <dylanturn@gmail.com>

RUN yum -y update; yum clean all
RUN yum -y install epel-release; yum clean all
RUN yum -y install httpd
RUN service httpd start
RUN yum -y install php

RUN chkconfig httpd on
RUN rm -f /var/www/html/info.php
RUN ECHO <?php phpinfo(); ?> > /var/www/html/info.php

RUN service httpd restart

USER user

EXPOSE 80 5000
WORKDIR /projects
ENTRYPOINT ["/home/user/entrypoint.sh"]

CMD tail -f /dev/null