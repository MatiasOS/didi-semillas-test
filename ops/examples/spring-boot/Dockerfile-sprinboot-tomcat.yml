FROM tomcat:9.0-jre8-alpine

RUN rm -rf $CATALINA_HOME/webapps/ROOT 

COPY target/*.war $CATALINA_HOME/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]