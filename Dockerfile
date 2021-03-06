FROM tomcat:9-jdk11-openjdk

RUN rm -rf $CATALINA_HOME/webapps/ROOT

COPY target/*.war $CATALINA_HOME/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
