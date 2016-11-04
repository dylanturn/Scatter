FROM maven:3-jdk-8
MAINTAINER Dylan Turnbull <dylanturn@gmail.com>

RUN systemctl disable firewalld
RUN yum -y install git

ADD entrypoint.sh /entrypoint.sh
RUN chmod -v +x /entrypoint.sh
CMD ["/entrypoint.sh"]